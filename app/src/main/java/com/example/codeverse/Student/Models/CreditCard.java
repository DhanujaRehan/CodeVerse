package com.example.codeverse.Student.Models;

import com.example.codeverse.R;

public class CreditCard {
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String bankName;
    private CardType cardType;
    private int backgroundResId;
    private boolean isSelected;

    public enum CardType {
        VISA, MASTERCARD, AMEX, DISCOVER
    }

    public CreditCard(String cardNumber, String cardHolder, String expiryDate,
                      String bankName, CardType cardType, int backgroundResId) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiryDate = expiryDate;
        this.bankName = bankName;
        this.cardType = cardType;
        this.backgroundResId = backgroundResId;
        this.isSelected = false;
    }

    public String getFormattedCardNumber() {
        return "•••• •••• •••• " + cardNumber.substring(cardNumber.length() - 4);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public void setBackgroundResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getCardTypeImage() {
        switch (cardType) {
            case MASTERCARD:
                return R.drawable.ic_mastercard_logo;
            case AMEX:
                return R.drawable.ic_amex_logo;
            case DISCOVER:
                return R.drawable.ic_discover_logo;
            case VISA:
            default:
                return R.drawable.ic_visa_logo;
        }
    }
}