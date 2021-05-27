package com.mars.model;

import java.util.Collection;

import javax.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Setter
@Getter
@ToString(exclude = "password")
@Entity
@Table(name = "member")
public class MemberDto implements UserDetails {
 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "no")
	int no;

	@Id
	@Column(name = "id", length = 50)
	private String id;

	@Column(name = "name", length = 200, nullable = false)
	private String name;


	@Column(name = "password", length = 200, nullable = false)
	private String password;


	@Column(name = "profile_image", length = 400, nullable = false)
	private String profile_image;


	@Lob
	private String android_token;


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.getId();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}