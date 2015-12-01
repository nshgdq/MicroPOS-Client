package ow.micropos.client.desktop.model.error;

import lombok.Data;

@Data
public class ErrorInfo {

    String status = "";

    String message = "";

    public ErrorInfo() {}

    public ErrorInfo(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
