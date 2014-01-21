package edu.harvard.iq.dataverse.engine;

import edu.harvard.iq.dataverse.*;
import edu.harvard.iq.dataverse.engine.command.CommandContext;

/**
 * A base CommandContext for tests. Provides no-op implementations. Should probably be
 * overridden for actual tests.
 * 
 * @author michael
 */
public class TestCommandContext implements CommandContext {

	@Override
	public DatasetServiceBean datasets() {
		return null;
	}

	@Override
	public DataverseServiceBean dataverses() {
		return null;
	}

	@Override
	public DataverseRoleServiceBean roles() {
		return null;
	}

	@Override
	public DataverseUserServiceBean users() {
		return null;
	}

	@Override
	public IndexServiceBean indexing() {
		return null;
	}

	@Override
	public SearchServiceBean search() {
		return null;
	}

	@Override
	public PermissionServiceBean permissions() {
		return null;
	}
	
}