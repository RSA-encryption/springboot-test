package com.CODEXIS.PopularDocuments.Scenarios;

import Core.BaseRestScenario;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.sql.ResultSet;

class VisitScenario extends BaseRestScenario {
	@Test
	public void visitScenario() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/visit")
				.queryParam("docUUID", "85d24930-303d-4c66-8d83-db349a210bdf");
		MvcResult restResult = mvc.perform(requestBuilder).andReturn();
		Integer statusCode = restResult.getResponse().getStatus();
		ResultSet result = performQuery("SELECT * FROM codexis.visits WHERE visits.doc_uuid = '85d24930-303d-4c66-8d83-db349a210bdf' ORDER BY visits.timestamp DESC");
		Assert.assertEquals(statusCode == 201 && result.last() ? result.getRow() : 0 == 1, 1);
	}
}
