package encalma.backendcalma3.DTO;

public class ApiResponse<T> {
    private String status;
    private String mensaje;
    private T datos;

    public ApiResponse(String status, String mensaje, T datos) {
        this.status = status;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public String getStatus() {
        return status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public T getDatos() {
        return datos;
    }
}
