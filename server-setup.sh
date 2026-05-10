#!/bin/bash
# ─── Sunucuya İlk Kurulum Script'i ───────────────────────────────────────────
# Kullanım: bash server-setup.sh
# Sadece bir kez çalıştır!

set -e

echo " Docker kuruluyor..."
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

echo " Uygulama dizini oluşturuluyor..."
sudo mkdir -p /opt/ems
sudo chown $USER:$USER /opt/ems
cd /opt/ems

echo " .env dosyasını oluştur ve düzenle:"
echo "   nano /opt/ems/.env"
echo ""
echo "Örnek .env içeriği:"
cat <<'EOF'
DB_PASSWORD=guclu_sifre_buraya
EVENT_DB_PASSWORD=baska_guclu_sifre
JWT_SECRET=$(openssl rand -base64 64)
JWT_EXPIRATION=3600000
ADMIN_USERNAMES=admin
ADMIN_PASSWORDS=admin_sifresi
RESEND_API_KEY=re_xxxxx
KAFKA_CLUSTER_ID=$(openssl rand -hex 16)
CORS_ALLOWED_ORIGINS=https://akissy.com
GHCR_USERNAME=berkayerdemsoy
EOF

echo ""
echo "✅ Kurulum tamamlandı!"
echo "   Şimdi /opt/ems/.env dosyasını oluştur ve GitHub Secrets'ları tanımla."
echo ""
echo " GitHub'da şu Secrets'ları tanımlamalısın:"
echo "   SSH_HOST        → Sunucu IP adresi"
echo "   SSH_USER        → SSH kullanıcı adı (ubuntu, root vb.)"
echo "   SSH_PRIVATE_KEY → Sunucuya bağlantı için private key"
echo ""
echo " SSH key oluşturma (lokal makinende çalıştır):"
echo "   ssh-keygen -t ed25519 -C 'github-actions-deploy'"
echo "   # Public key'i sunucuya ekle:"
echo "   ssh-copy-id -i ~/.ssh/id_ed25519.pub user@sunucu-ip"
echo "   # Private key'i GitHub Secret olarak kaydet (SSH_PRIVATE_KEY)"
