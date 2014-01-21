package edu.harvard.iq.dataverse;

import edu.harvard.iq.dataverse.engine.Permission;
import edu.harvard.iq.dataverse.util.BitSet;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

/**
 * A role is an annotated set of permissions. A role belongs
 * to a {@link Dataverse}. Users may assume roles from the current dataverse,
 * or from its parent dataverses, up to the first permission root dataverse.
 * 
 * @author michael
 */
@NamedQueries({
	@NamedQuery(name = "DataverseRole.findByOwnerId",
			    query= "SELECT r FROM DataverseRole r WHERE r.owner.id=:ownerId ORDER BY r.name"),
	@NamedQuery(name = "DataverseRole.deleteById",
			    query= "DELETE FROM DataverseRole r WHERE r.id=:id")
})
@Entity
public class DataverseRole implements Serializable  {
	
	public static Set<Permission> permissionSet( Iterable<DataverseRole> roles ) {
		long miniset = 0l;
		for ( DataverseRole role : roles ) {
			miniset |= role.permissionBits;
		}
		return new BitSet(miniset).asSetOf(Permission.class);
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
	@Size(max = 1000, message = "Description must be at most 1000 characters.")
    private String description;
    
	@NotBlank(message = "Please enter an alias.")
    @Size(max = 16, message = "Alias must be at most 16 characters.")
    @Pattern(regexp = "[a-zA-Z0-9\\_\\-]*", message = "Found an illegal character(s). Valid characters are a-Z, 0-9, '_', and '-'.")
    private String alias;
	
	@OneToMany( cascade={CascadeType.MERGE, CascadeType.REMOVE},
			fetch = FetchType.LAZY	)
	private Set<UserDataverseAssignedRole> assignedRoles;
	
	/** Stores the permissions in a bit set.  */
	private long permissionBits;
	
	@ManyToOne
    @JoinColumn(nullable=false)     
    private Dataverse owner;
	
	public void registerAssignedRole( UserDataverseAssignedRole udr ) {
		if ( assignedRoles == null ) {
			assignedRoles = new HashSet<>();
		}
		assignedRoles.add(udr);
	}
	
	public void deregisterAssignedRole( UserDataverseAssignedRole udr ) {
		if ( assignedRoles != null ) {
			assignedRoles.remove(udr);
		}
	}
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Dataverse getOwner() {
		return owner;
	}

	public void setOwner(Dataverse owner) {
		this.owner = owner;
	}
	
	public void addPermission( Permission p ) {
		permissionBits = new BitSet(permissionBits).set(p.ordinal()).getBits();
	}
	
	public void clearPermissions() {
		permissionBits = 0l;
	}
	
	public Set<Permission> permissions() {
		return new BitSet(permissionBits).asSetOf(Permission.class);
	}
	
}