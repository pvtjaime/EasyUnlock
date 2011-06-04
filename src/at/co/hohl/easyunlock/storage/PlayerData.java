package at.co.hohl.easyunlock.storage;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Represents a persistence dao for storing player information.
 *
 * @author Michael Hohl
 */
@Entity()
@Table(name = "eu_player")
public class PlayerData {
    @Id
    private int id;

    @NotEmpty
    @Length(max = 32)
    private String name;

    @Length(max = 32)
    private String invitedBy;

    @Length(max = 32)
    private String unlockedBy;

    private boolean unlocked = false;

    private boolean rulesAccepted = false;

    private Date acceptedDated;

    private int advertised = 0;

    private int goldCredit = 0;

    /** Allocates a new PlayerData. */
    public PlayerData() {
    }

    public Date getAcceptedDated() {
        return acceptedDated;
    }

    public void setAcceptedDated(Date acceptedDated) {
        this.acceptedDated = acceptedDated;
    }

    public int getAdvertised() {
        return advertised;
    }

    public void setAdvertised(int advertised) {
        this.advertised = advertised;
    }

    public int getGoldCredit() {
        return goldCredit;
    }

    public void setGoldCredit(int goldCredit) {
        this.goldCredit = goldCredit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRulesAccepted() {
        return rulesAccepted;
    }

    public void setRulesAccepted(boolean rulesAccepted) {
        this.rulesAccepted = rulesAccepted;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getUnlockedBy() {
        return unlockedBy;
    }

    public void setUnlockedBy(String unlockedBy) {
        this.unlockedBy = unlockedBy;
    }
}
