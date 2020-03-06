package com.imaginology.texas.Counseling.ProcessCounseling;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProcessCounselDto {
    @SerializedName("discountFee")
    @Expose
    private Long discountFee;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("totalFee")
    @Expose
    private Long totalFee;

    public ProcessCounselDto(Long discountFee, String remarks, Long totalFee) {
        this.discountFee = discountFee;
        this.remarks = remarks;
        this.totalFee = totalFee;
    }

    public Long getDiscountFee() {
        return discountFee;
    }

    public void setDiscountFee(Long discountFee) {
        this.discountFee = discountFee;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }
}
