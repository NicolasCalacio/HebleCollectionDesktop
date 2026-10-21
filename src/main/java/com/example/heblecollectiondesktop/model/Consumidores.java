package com.example.heblecollectiondesktop.model;

public class Consumidores {
    private int IDconsumidor;
    private String nomconsumidor;
    private String cpfconsumidor;
    private String senhaconsumidor;
    private String emailconsumidor;
    private String celularconsumidor;
    private String cepconsumidor;
    private String enderecoconsumidor;
    private String dataNascconsumidor;
    private String statusconsumidor;


    public Consumidores() {
    }


    public Consumidores(int IDconsumidor, String nomconsumidor, String cpfconsumidor, String senhaconsumidor, String emailconsumidor, String celularconsumidor, String cepconsumidor, String enderecoconsumidor, String dataNascconsumidor, String statusconsumidor) {
        this.IDconsumidor = IDconsumidor;
        this.nomconsumidor = nomconsumidor;
        this.cpfconsumidor = cpfconsumidor;
        this.senhaconsumidor = senhaconsumidor;
        this.emailconsumidor = emailconsumidor;
        this.celularconsumidor = celularconsumidor;
        this.cepconsumidor = cepconsumidor;
        this.enderecoconsumidor = enderecoconsumidor;
        this.dataNascconsumidor = dataNascconsumidor;
        this.statusconsumidor = statusconsumidor;
    }

    public int getIDconsumidor() {
        return IDconsumidor;
    }

    public void setIDconsumidor(int IDconsumidor) {
        this.IDconsumidor = IDconsumidor;
    }

    public String getNomconsumidor() {
        return nomconsumidor;
    }

    public void setNomconsumidor(String nomconsumidor) {
        this.nomconsumidor = nomconsumidor;
    }

    public String getCpfconsumidor() {
        return cpfconsumidor;
    }

    public void setCpfconsumidor(String cpfconsumidor) {
        this.cpfconsumidor = cpfconsumidor;
    }

    public String getSenhaconsumidor() {
        return senhaconsumidor;
    }

    public void setSenhaconsumidor(String senhaconsumidor) {
        this.senhaconsumidor = senhaconsumidor;
    }

    public String getEmailconsumidor() {
        return emailconsumidor;
    }

    public void setEmailconsumidor(String emailconsumidor) {
        this.emailconsumidor = emailconsumidor;
    }

    public String getCelularconsumidor() {
        return celularconsumidor;
    }

    public void setCelularconsumidor(String celularconsumidor) {
        this.celularconsumidor = celularconsumidor;
    }

    public String getCepconsumidor() {
        return cepconsumidor;
    }

    public void setCepconsumidor(String cepconsumidor) {
        this.cepconsumidor = cepconsumidor;
    }

    public String getEnderecoconsumidor() {
        return enderecoconsumidor;
    }

    public void setEnderecoconsumidor(String enderecoconsumidor) {
        this.enderecoconsumidor = enderecoconsumidor;
    }

    public String getDataNascconsumidor() {
        return dataNascconsumidor;
    }

    public void setDataNascconsumidor(String dataNascconsumidor) {
        this.dataNascconsumidor = dataNascconsumidor;
    }

    public String getStatusconsumidor() {
        return statusconsumidor;
    }

    public void setStatusconsumidor(String statusconsumidor) {
        this.statusconsumidor = statusconsumidor;
    }

    public String getDataNascFormatada() {
        if (dataNascconsumidor == null || dataNascconsumidor.isEmpty()) {
            return "";
        }
        try {

            if (dataNascconsumidor.length() >= 10) {
                String ano = dataNascconsumidor.substring(0, 4);
                String mes = dataNascconsumidor.substring(5, 7);
                String dia = dataNascconsumidor.substring(8, 10);
                return dia + "/" + mes + "/" + ano;
            }
            return dataNascconsumidor;
        } catch (Exception e) {
            return dataNascconsumidor;
        }
    }

}
