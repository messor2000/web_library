package kpi.diploma.ovcharenko.entity.card;

public enum CardStatus {
    WAIT_FOR_APPROVE("wait for approve"), APPROVED("approved"), BOOK_RETURNED("book returned");

    public final String statusName;

    CardStatus(String status) {
        this.statusName = status;
    }
}
