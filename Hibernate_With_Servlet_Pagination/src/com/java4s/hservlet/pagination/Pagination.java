package com.java4s.hservlet.pagination;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class Pagination extends HttpServlet {

	private static final long serialVersionUID = 1L;
	SessionFactory factory;

	public void init(ServletConfig config) throws ServletException {
		factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		System.out.println("Factory has been created....");
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		int pageIndex = 0;
		int totalNumberOfRecords = 0;
		int numberOfRecordsPerPage = 4;
		
		String productName = (req.getParameter("productName") == null)? "" : req.getParameter("productName").trim();
		String sPageIndex = req.getParameter("pageIndex");

		if (sPageIndex == null) {
			pageIndex = 1;
		} else {
			pageIndex = Integer.parseInt(sPageIndex);
		}

		Session ses = factory.openSession();
		int s = (pageIndex * numberOfRecordsPerPage) - numberOfRecordsPerPage;

		Criteria crit = ses.createCriteria(Product.class);
		if(!"".equals(productName)) {
			crit.add(Restrictions.like("proName", productName));
		}
		crit.setFirstResult(s);
		crit.setMaxResults(numberOfRecordsPerPage);

		@SuppressWarnings("unchecked")
		List<Product> l = crit.list();
		Iterator<Product> it = l.iterator();

		PrintWriter pw = res.getWriter();
		pw.println("<table border=1>");
		pw.println("<tr>");
		pw.println("<th>PID</th><th>PNAME</th><th>PRICE</th>");
		pw.println("</tr>");

		while (it.hasNext()) {
			Product p = it.next();
			pw.println("<tr>");
			pw.println("<td>" + p.getProductId() + "</td>");
			pw.println("<td>" + p.getProName() + "</td>");
			pw.println("<td>" + p.getPrice() + "</td>");
			pw.println("</tr>");
		}

		pw.println("<table>");

		Criteria crit1 = ses.createCriteria(Product.class);
		if(!"".equals(productName)) {
			crit1.add(Restrictions.like("proName", productName));
		}
		crit1.setProjection(Projections.rowCount());

		@SuppressWarnings("unchecked")
		List<Product> l1 = crit1.list();

		//pw.println(l1.size());
		// returns 1, as list() is used to execute the query if true will
		// returns 1

		Iterator<Product> it1 = l1.iterator();

		if (it1.hasNext()) {
			Object o = it1.next();
			totalNumberOfRecords = Integer.parseInt(o.toString());
		}

		int noOfPages = totalNumberOfRecords / numberOfRecordsPerPage;
		if (totalNumberOfRecords > (noOfPages * numberOfRecordsPerPage)) {
			noOfPages = noOfPages + 1;
		}

		for (int i = 1; i <= noOfPages; i++) {
			if(i == pageIndex) {
				pw.println(i);
			}
			else {
				String myurl = "ind?pageIndex=" + i;
				pw.println("<a href=" + myurl + ">" + i + "</a>");
			}
		}

		ses.close();
		pw.close();

	}

	public void destroy() {
		factory.close();
	}

}
