package com.mercadolibre.socialmeli.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"user_id", "user_name", "user_email"})
public class User {

    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_email")
    private String userEmail;
    @JsonIgnore // para evitar ciclos infinitos
    private Set<Integer> follows = new HashSet<>();
}
