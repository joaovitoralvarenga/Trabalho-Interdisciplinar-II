package Response;

import com.google.gson.JsonElement;

public class StandardResponse {
    
    // Definir ENUM de StatusResponse
    public enum StatusResponse {
        SUCCESS("Success"),
        ERROR("Error");

        @SuppressWarnings("unused")
        private String status;

        private StatusResponse(String status) {
            this.status = status;
        }
    }

    // Definir atributos da classe StandardResponse
    private StatusResponse status;
    private String message;
    private JsonElement data;

    // Construtores
    public StandardResponse(StatusResponse status) {
        this.status = status;
        this.message = "";
        this.data = null;
    }

    public StandardResponse(StatusResponse status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public StandardResponse(StatusResponse status, JsonElement data) {
        this.status = status;
        this.message = "";
        this.data = data;        
    }

    public StandardResponse(StatusResponse status, String message, JsonElement data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters e Setters
    public StatusResponse getStatus() {
        return this.status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonElement getData() {
        return this.data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }
}
