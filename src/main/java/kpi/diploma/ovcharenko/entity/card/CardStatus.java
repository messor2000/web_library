package kpi.diploma.ovcharenko.entity.card;

public enum CardStatus {
    WAIT_FOR_APPROVE("WAIT_FOR_APPROVE"), APPROVED("APPROVED"), BOOK_RETURNED("BOOK_RETURNED");

    public final String statusName;

    CardStatus(String status) {
        this.statusName = status;
    }
}
