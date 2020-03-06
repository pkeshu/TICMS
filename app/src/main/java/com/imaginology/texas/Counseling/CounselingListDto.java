package com.imaginology.texas.Counseling;

/**
 * Created by yubar on 5/25/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CounselingListDto {
    @SerializedName("total")
    @Expose
    private Long total;
    @SerializedName("data")
    @Expose
    List<CounselingDetailListDto> counselingDetailListDtos;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<CounselingDetailListDto> getCounselingDetailListDtos() {
        return counselingDetailListDtos;
    }

    public void setCounselingDetailListDtos(List<CounselingDetailListDto> counselingDetailListDtos) {
        this.counselingDetailListDtos = counselingDetailListDtos;
    }
}