package com.booking.model;

import java.math.BigDecimal;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CT_BOOKING_REGISTER_ORACLE")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	@Column(name = "region_code")
    private String regionCode;

    @Column(name = "controlling_code")
    private String controllingCode;

    @Column(name = "pan_no")
    private String panNo;

    @Column(name = "bkgbrn")
    private String bkgbrn;

    @Id
    @Column(name = "dwbno")
    private String dwbno;

//    @Temporal(TemporalType.DATE)
    @Column(name = "dwbdt")
    private Date dwbdt;

    @Column(name = "dly_brn")
    private String dlyBrn;

    @Column(name = "custcode")
    private String custcode;

    @Column(name = "cnor")
    private String cnor;

    @Column(name = "cnee")
    private String cnee;

    @Column(name = "odaloc")
    private String odaloc;

    @Column(name = "pkg")
    private Integer pkg;

    @Column(name = "wt")
    private Double wt;

    @Column(name = "frt")
    private Double frt;

    @Column(name = "fov")
    private Double fov;

    @Column(name = "st")
    private Double st;

    @Column(name = "ops")
    private Double ops;

    @Column(name = "fuel")
    private Double fuel;

    @Column(name = "hand")
    private Double hand;

    @Column(name = "dacc")
    private Double dacc;

    @Column(name = "codfod")
    private Double codfod;

    @Column(name = "rbk")
    private Double rbk;

    @Column(name = "rbkhand")
    private Double rbkhand;

    @Column(name = "cod")
    private Double cod;

    @Column(name = "misc")
    private Double misc;

    @Column(name = "dkt")
    private Double dkt;

    @Column(name = "tds")
    private Double tds;

    @Column(name = "ser")
    private Double ser;

    @Column(name = "cess")
    private Double cess;

    @Column(name = "oda")
    private Double oda;

    @Column(name = "tfrt")
    private Double tfrt;

    @Column(name = "declared_value")
    private BigDecimal declaredValue;

    @Column(name = "basis")
    private int basis;

    @Column(name = "cqno")
    private String cqno;

    @Temporal(TemporalType.DATE)
    @Column(name = "cqdt")
    private Date cqdt;

    @Column(name = "bacode")
    private String bacode;

    @Column(name = "div_code")
    private Integer divCode;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update")
    private Date lastUpdate;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insert_date")
    private Date insertDate;

    @Column(name = "bkg_basis")
    private String bkgBasis;

    @Column(name = "dly_region")
    private String dlyRegion;

    @Column(name = "sb_tax")
    private Double sbTax;

    @Column(name = "actual_weight")
    private Double actualWeight;

    @Column(name = "oda_type")
    private String odaType;

    @Column(name = "kk_cess")
    private Double kkCess;

    @Column(name = "cgst")
    private Double cgst;

    @Column(name = "sgst")
    private Double sgst;

    @Column(name = "igst")
    private Double igst;

    @Column(name = "gstin")
    private String gstin;

//    @Temporal(TemporalType.DATE)
    @Column(name = "purchase_order_date")
    private Date purchaseOrderDate;

}
