package kpi.diploma.ovcharenko.entity.book.status;

public enum Status {
    FREE("free"), BOOKED("booked"), TAKEN("taken");

    public final String statusName;

    Status(String status) {
        this.statusName = status;
    }
}
