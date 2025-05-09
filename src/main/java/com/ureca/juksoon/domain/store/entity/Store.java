package com.ureca.juksoon.domain.store.entity;

import com.ureca.juksoon.domain.common.BaseEntity;
import com.ureca.juksoon.domain.store.dto.request.StoreCreateReq;
import com.ureca.juksoon.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "store")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "description")
    private String description;
    @Column(name = "logo_image_url")
    private String logoImageURL;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void updateStore(String name, String address, String description, String logoImageURL) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.logoImageURL = logoImageURL;
    }
}
