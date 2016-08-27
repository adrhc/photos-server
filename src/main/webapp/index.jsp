<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<%--<c:catch>--%>
    <%--<sql:update var="rs" dataSource="exifweb">--%>
        <%--INSERT INTO Album (name) values (CONCAT('album ', NOW()))--%>
    <%--</sql:update>--%>
<%--</c:catch>--%>

<sql:query var="rs" dataSource="exifweb">
    select CONVERT(i.id, CHAR(10)) as imageId,
            CONVERT(a.id, CHAR(10)) as albumId,
            CONCAT(i.name, '') as image, CONCAT(a.name, '') as album
    from Image i
    join Album a on a.id = i.fk_album
    LIMIT 10
</sql:query>

<html>
<head>
    <title>DB Test</title>
    <style type="text/css">
        .top {
            border-top:thin solid;
            border-color:black;
        }

        .bottom {
            border-bottom:thin solid;
            border-color:black;
        }

        .left {
            border-left:thin solid;
            border-color:black;
        }

        .right {
            border-right:thin solid;
            border-color:black;
        }
    </style>
</head>
<body>

<a href="app/json/appconfig/reloadParams">Reload app config</a><br>
<a href="app/json/action/exif">Refresh all albums</a><br>
<a href="app/json/action/exif?name=2012-11-07_Amalia">Refresh 2012-11-07_Amalia</a><br>
<a href="app/json/image/countPages?albumId=1">Album 1 page count</a><br>
<a href="app/json/image?albumId=1&pageNr=1">Album 1 page 1</a><br>
<a href="app/json/album">Toate albumele</a><br>
<h2>Results</h2>
<c:if test="${empty rs.rows}">No results !</c:if>

<table style="cellspacing:0;margin: 3px">
    <tr>
        <th class="top left">id</th>
        <th class="top left">image</th>
        <th class="top left right">album</th>
    </tr>
<c:forEach var="row" items="${rs.rows}">
    <tr>
        <td class="top left">${row['imageId']}</td>
        <td class="top left"><a href="app/json/image/${row['imageId']}">${row['image']}</a></td>
        <td class="top left right"><a href="app/json/album/${row['albumId']}">${row['album']}</a></td>
    </tr>
</c:forEach>
</table>
</body>
</html>