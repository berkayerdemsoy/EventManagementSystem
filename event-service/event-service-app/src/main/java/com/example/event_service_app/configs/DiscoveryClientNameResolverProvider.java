package com.example.event_service_app.configs;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.Status;
import io.grpc.StatusOr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Bridges Spring Cloud DiscoveryClient to gRPC's NameResolver SPI for discovery:/// URIs.
 */
@Slf4j
public class DiscoveryClientNameResolverProvider extends NameResolverProvider {

    private static final String SCHEME = "discovery";
    private static final String GRPC_PORT_METADATA_KEY = "grpc.port";

    private final DiscoveryClient discoveryClient;

    public DiscoveryClientNameResolverProvider(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (!SCHEME.equals(targetUri.getScheme())) {
            return null;
        }

        String serviceId = extractServiceId(targetUri);
        if (serviceId == null || serviceId.isBlank()) {
            return null;
        }

        return new DiscoveryClientNameResolver(serviceId, discoveryClient);
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 6;
    }

    private String extractServiceId(URI targetUri) {
        String path = targetUri.getPath();
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path != null && !path.isBlank()) {
            return path;
        }
        return targetUri.getHost();
    }

    private static final class DiscoveryClientNameResolver extends NameResolver {

        private final String serviceId;
        private final DiscoveryClient discoveryClient;
        private Listener2 listener;

        private DiscoveryClientNameResolver(String serviceId, DiscoveryClient discoveryClient) {
            this.serviceId = serviceId;
            this.discoveryClient = discoveryClient;
        }

        @Override
        public String getServiceAuthority() {
            return serviceId;
        }

        @Override
        public void start(Listener2 listener) {
            this.listener = listener;
            resolve();
        }

        @Override
        public void refresh() {
            resolve();
        }

        @Override
        public void shutdown() {
            // no resources to dispose
        }

        private void resolve() {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances.isEmpty()) {
                listener.onResult(NameResolver.ResolutionResult.newBuilder()
                        .setAddressesOrError(
                                StatusOr.fromStatus(
                                        Status.UNAVAILABLE.withDescription("No instances available for: " + serviceId)
                                )
                        )
                        .build());
                return;
            }

            List<EquivalentAddressGroup> addresses = instances.stream()
                    .map(this::toAddressGroup)
                    .filter(Objects::nonNull)
                    .toList();

            if (addresses.isEmpty()) {
                listener.onResult(NameResolver.ResolutionResult.newBuilder()
                        .setAddressesOrError(
                                StatusOr.fromStatus(
                                        Status.UNAVAILABLE.withDescription("No valid gRPC addresses for: " + serviceId)
                                )
                        )
                        .build());
                return;
            }

            listener.onResult(NameResolver.ResolutionResult.newBuilder()
                    .setAddressesOrError(StatusOr.fromValue(addresses))
                    .setAttributes(Attributes.EMPTY)
                    .build());
        }

        private EquivalentAddressGroup toAddressGroup(ServiceInstance instance) {
            Integer port = resolveGrpcPort(instance);
            if (port == null) {
                return null;
            }
            return new EquivalentAddressGroup(new InetSocketAddress(instance.getHost(), port));
        }

        private Integer resolveGrpcPort(ServiceInstance instance) {
            String grpcPortFromMetadata = instance.getMetadata() != null
                    ? instance.getMetadata().get(GRPC_PORT_METADATA_KEY)
                    : null;

            if (grpcPortFromMetadata == null || grpcPortFromMetadata.isBlank()) {
                return instance.getPort();
            }

            try {
                return Integer.parseInt(grpcPortFromMetadata);
            } catch (NumberFormatException ex) {
                log.warn("Invalid '{}' metadata '{}' for service '{}' instance '{}', falling back to instance port {}",
                        GRPC_PORT_METADATA_KEY,
                        grpcPortFromMetadata,
                        serviceId,
                        instance.getHost(),
                        instance.getPort());
                return instance.getPort();
            }
        }
    }
}


