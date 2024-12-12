package com.qred.internalpaymentservice.client;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A value object representing an client's details (firstName, lastName)
 *
 * @author georgemore on 2024-12-10
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClientDetails {

  private String firstName;
  private String lastName;
}
