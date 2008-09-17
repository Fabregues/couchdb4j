package com.fourspaces.couchdb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import com.fourspaces.couchdb.View;
import com.fourspaces.couchdb.ViewResults;

public class ViewTest {
  
  Log log = LogFactory.getLog(getClass());
	Session sess = TestSession.getTestSession();
	Database foo;
	
	@Before public void createTestDB() throws Exception {
		sess.createDatabase("foo");
		foo=sess.getDatabase("foo");
		Document d = new Document();
		d.put("foo","bar");
		foo.saveDocument(d);
		log.debug("known id:"+d.getId());
		log.debug(foo.getDocument(d.getId()));
		
		foo.saveDocument(new Document());
		foo.saveDocument(new Document());
		foo.saveDocument(new Document());
		foo.saveDocument(new Document());
	}
	
	@After public void removeTestDB() {
		sess.deleteDatabase("foo");
	}

	@Test public void adhoc() {

		int all = foo.getAllDocuments().getResults().size();
		int adhoc = foo.adhoc("function (doc) {emit(null, doc);}").getResults().size();
		assertEquals(all,adhoc);
		
		
	}
	
	@Test public void adhoc2() {
		int adhoc = foo.adhoc("function (doc){ if (doc.foo=='bar'){ emit(doc, doc)}}").getResults().size();
		assertEquals(1,adhoc);
	}
	
	@Test
	public void addNamed() throws Exception {
		Document d = new Document();
		d.put("foo","bar");

		log.debug("Saving d");
		foo.saveDocument(d);
		
		Document d2 = new Document();
		//d2.put("foo","baz");
	//	d2.addView("all_documents", "function (doc){ return doc; }");
		d2.addView("viewfoobar", "testview", "function (doc){ if (doc.foo=='bar'){ return doc; }}");
		log.debug("Saving d2 - "+d2.getId()+" - "+d2.toString());
		foo.saveDocument(d2);
		log.debug("Saved d2  - "+d2.getId()+" - "+d2.toString());
		Document d2_2 = foo.getDocument(d2.getId());
		log.debug("Saved d2_2 - "+d2_2.toString());
		
		/*
		 * Doesn't work - CouchDB doens't support named views yet.
		 */
		assertNotNull(d2_2.getView("viewfoobar/testview"));
		assertEquals(2,foo.view(d2.getView("viewfoobar/testview")).getResults().size());
		
		foo.deleteDocument(d);
		foo.deleteDocument(d2);
		
		
	}
	
	

	
	
	
}