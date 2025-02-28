package dev.aj.spring_modulith.order.entities.types;

public enum OrderStatus {
    OPEN("O"),
    CLOSED("C"),
    SHIPPED("S"),
    DELIVERED("D"),
    UNKNOWN("U");

    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }

    public static OrderStatus fromCode(String code){
        for(OrderStatus orderStatus : OrderStatus.values()){
            if(orderStatus.code.equals(code)){
                return orderStatus;
            }
        }
        return OrderStatus.UNKNOWN;
    }
}
