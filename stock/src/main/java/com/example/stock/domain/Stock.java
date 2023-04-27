package com.example.stock.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    @Version
    private Long version;

    public void decrease(Long quantity){
        if(this.quantity - quantity < 0){
            throw new RuntimeException("foo");
        }

        this.quantity -= quantity;
    }

}
