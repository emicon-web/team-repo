package com.bezkoder.springjwt.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.bezkoder.springjwt.utils.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String token;
    
    @Column(length = 6, nullable = false, name = "passcode")
    @JsonProperty("tokenCred")
    private Integer passcode;

    @NotNull
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private User user;

    @NotNull
    private Date expiryDate;

    public Token() {
        super();
    }

    public Token(User user, final String token, final Integer tokenCred, final int expiryTimeInMinutes) {
		super();
		this.user = user;
		this.token = token;
		this.passcode = tokenCred;
		this.expiryDate = Utils.calculateExpiryDate(expiryTimeInMinutes);
	}



	public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

   public Integer getTokenCred() {
		return passcode;
	}

	public void setTokenCred(Integer tokenCred) {
		this.passcode = tokenCred;
	}

	public void setExpiryDate(final int expiryTimeInMinutes) {
		this.expiryDate = Utils.calculateExpiryDate(expiryTimeInMinutes);
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getExpiryDate() == null) ? 0 : getExpiryDate().hashCode());
        result = prime * result + ((getToken() == null) ? 0 : getToken().hashCode());
        result = prime * result + ((getUser() == null) ? 0 : getUser().hashCode());
        result = prime * result + ((getTokenCred() == null) ? 0 : getTokenCred().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (getExpiryDate() == null) {
            if (other.getExpiryDate() != null) {
                return false;
            }
        } else if (!getExpiryDate().equals(other.getExpiryDate())) {
            return false;
        }
        if (getToken() == null) {
            if (other.getToken() != null) {
                return false;
            }
        } else if (!getToken().equals(other.getToken())) {
            return false;
        }
        if (getUser() == null) {
            if (other.getUser() != null) {
                return false;
            }
        } else if (!getUser().equals(other.getUser())) {
            return false;
        }
        if (getTokenCred() == null) {
            if (other.getTokenCred() != null) {
                return false;
            }
        } else if (!getTokenCred().equals(other.getTokenCred())) {
            return false;
        }
        return true;
    }

    @Override
	public String toString() {
		return "Token [token=" + token + ", passcode=" + passcode + ", user=" + user + ", expiryDate=" + expiryDate
				+ "]";
	}

}
