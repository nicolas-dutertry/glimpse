<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.glimpse.org/glimpse" prefix="glimpse" %>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevent caching at the proxy server
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="gwt:property" content="locale=<glimpse:locale/>">

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="themes/<glimpse:theme/>/style.css">
	<link rel="shortcut icon" href="themes/<glimpse:theme/>/favicon.ico"/>
	
    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Glimpse News Aggregator</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="glimpse_gwt/glimpse_gwt.nocache.js"></script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body class="glimpse">

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

	<div id="main"></div>
	
	<glimpse:themes-list var="themesList"/>
	
	<c:forEach var="theme" varStatus="status" items="${themesList}">
		<input type="hidden" name="theme_${status.index}" id="theme_${status.index}" value="${theme}"/>
	</c:forEach>
	
	<input type="hidden" name="default-page" id="default-page" value="true"/>
	<input type="hidden" name="locale" id="locale" value="<glimpse:locale/>"/>
  </body>
</html>
