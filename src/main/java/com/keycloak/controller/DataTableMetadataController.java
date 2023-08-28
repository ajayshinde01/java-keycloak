package com.keycloak.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keycloak.dto.ColumnMetadata;
import com.keycloak.dto.ColumnType;
import com.keycloak.dto.DataTableMetadata;

@RestController
@RequestMapping("data-table-metadata")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DataTableMetadataController {
	private static final DataTableMetadata USER_DEFINATION_METADATA;
	private static final DataTableMetadata USER_ROLE_DEFINATION_METADATA;
	private static final DataTableMetadata SCOPE_DEFINATION_METADATA;

	static {

		USER_DEFINATION_METADATA = new DataTableMetadata()
				.addColumnMetadata(new ColumnMetadata("", "id", "id", ColumnType.RADIO, 10))
				.addColumnMetadata(new ColumnMetadata("FIRST NAME", "firstName", "firstName", ColumnType.TEXT, 30))
				.addColumnMetadata(new ColumnMetadata("LAST NAME", "lastName", "lastName", ColumnType.TEXT, 30))
				.addColumnMetadata(new ColumnMetadata("EMAIL", "email", "email", ColumnType.TEXT, 20))
				.addColumnMetadata(new ColumnMetadata("Enabled", "enabled", "enabled", ColumnType.TEXT, 10))
				;

		USER_ROLE_DEFINATION_METADATA = new DataTableMetadata()
				.addColumnMetadata(new ColumnMetadata("", "id", "id", ColumnType.RADIO, 10))
				.addColumnMetadata(new ColumnMetadata("NAME", "name", "name", ColumnType.TEXT, 45))
				.addColumnMetadata(new ColumnMetadata("DESCRIPTION", "description", "description", ColumnType.TEXT, 45))
				;
		
		SCOPE_DEFINATION_METADATA = new DataTableMetadata()
				.addColumnMetadata(new ColumnMetadata("", "id", "id", ColumnType.RADIO, 10))
				.addColumnMetadata(new ColumnMetadata("NAME", "name", "name", ColumnType.TEXT, 45))
				.addColumnMetadata(new ColumnMetadata("DISPLAY NAME", "displayName", "displayName", ColumnType.TEXT, 45))
				;
				

	}
	

	@GetMapping("user")
	public DataTableMetadata getUserDataTableMetadata() {
		return USER_DEFINATION_METADATA;
	}
	
	@GetMapping("userRoles")
	public DataTableMetadata getUserRoleDataTableMetadata() {
		return USER_ROLE_DEFINATION_METADATA;
	}
	
	@GetMapping("scope")
	public DataTableMetadata getScopeDataTableMetadata() {
		return SCOPE_DEFINATION_METADATA;
	}

}