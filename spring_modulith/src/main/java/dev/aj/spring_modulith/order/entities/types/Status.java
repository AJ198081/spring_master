package dev.aj.spring_modulith.order.entities.types;

public enum Status {
    OPEN("O"),
    CLOSED("C"),
    SHIPPED("S"),
    DELIVERED("D"),
    UNKNOWN("U");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    public static Status fromCode(String code){
        for(Status status : Status.values()){
            if(status.code.equals(code)){
                return status;
            }
        }
        return Status.UNKNOWN;
    }
}
