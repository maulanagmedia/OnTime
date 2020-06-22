package gmedia.net.id.OnTime.menu_reimburse;

public class ReimburseModel {
    String id, nama, tgl_pembayaran, foto, nominal, ket, approval, insert_at, status;

    public ReimburseModel(String id, String nama, String tgl_pembayaran, String foto, String nominal, String ket, String approval, String insert_at, String status){
        this.id = id;
        this.nama =nama;
        this.tgl_pembayaran = tgl_pembayaran;
        this.foto = foto;
        this.nominal = nominal;
        this.ket = ket;
        this.approval = approval;
        this.insert_at = insert_at;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTgl_pembayaran() {
        return tgl_pembayaran;
    }

    public void setTgl_pembayaran(String tgl_pembayaran) {
        this.tgl_pembayaran = tgl_pembayaran;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getInsert_at() {
        return insert_at;
    }

    public void setInsert_at(String insert_at) {
        this.insert_at = insert_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
