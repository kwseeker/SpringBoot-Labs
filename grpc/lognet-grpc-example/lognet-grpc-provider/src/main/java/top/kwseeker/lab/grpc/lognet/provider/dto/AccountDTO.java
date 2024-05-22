package top.kwseeker.lab.grpc.lognet.provider.dto;

public class AccountDTO {

    private String userId;
    private Integer amount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "userId='" + userId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
