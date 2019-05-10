/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.Serializable;

/**
 *
 * @author kismo
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String content;

    public Message(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public Message() {
        this.type = "TYPE";
        this.content = "CONTENT";
    }

    public Message(Message msg) {
        this.type = msg.getType();
        this.content = msg.getContent();
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
