package com.ibm.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import com.cloudant.client.api.Database;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.ibm.app.bean.Order;
import com.ibm.app.mgr.AppLabClientMgr;
import com.ibm.app.mgr.CloudantClientMgr;

/**
 * Simplified RESTful API to the Orders database.
 */
public class Orders extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Orders() {
		super();
	}

	/**
	 * Returns the list of current orders in JSON format.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		try {
			Database db = CloudantClientMgr.getInstance().getDB();
			List<Order> orders = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Order.class);
			Gson gson = new Gson();
			out.print(gson.toJson(orders));
		} catch (Throwable t) {
			out.println("got exception: " + t);
			t.printStackTrace(out);
			response.setStatus(503); // Service unavailable
		}
	}

	/**
	 * Adds an order to the database. Returns a JSON object containing the order id:
	 * 
	 * { id: 42 }
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if ("".equals(request.getParameter("description"))) return;
		
		Order order = new Order();
		order.setNumItems(Integer.parseInt(request.getParameter("num_items"), 10));
		order.setAmount(Double.parseDouble(request.getParameter("amount")));
		order.setDescription(request.getParameter("description"));
		order.setOrderId(Long.toString(System.currentTimeMillis()));
		order.setStatus("S");
		order.setDate(new Date());

		Database db = CloudantClientMgr.getInstance().getDB();
		PrintWriter out = response.getWriter();

		db.save(order);
		out.println("{ \"id\": \"" + order.getOrderId() + "\" }");

		try {
			AppLabClientMgr.getInstance().runProcess();
		} catch (Exception e) {
			throw new RuntimeException("Error running the process", e);
		}
	}
}
