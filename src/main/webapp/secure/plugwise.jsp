<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Habitatweave: Plugwise plugs </title>
	
    <script src="resources/js/jquery.js" ></script>
    <script src="resources/bootstrap/js/bootstrap.min.js"></script>
    
    <script src="http://responsivevoice.org/responsivevoice/responsivevoice.js"></script>
	
    <script src="resources/js/highcharts.js" ></script>
    <!-- For exporting chart as .pdf, e.t.c. -->
    <script src="http://code.highcharts.com/modules/exporting.js"></script>
    
    <!-- Bootstrap -->
    <link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Optional theme -->
    <link rel="stylesheet" href="resources/bootstrap/css/bootstrap-theme.min.css">
    <!-- Custom CSS -->
    <link href="resources/css/sb-admin.css" rel="stylesheet">
    <!-- Custom Fonts -->
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
    <!-- vvvvv this one not working..
    <link href="resources/font-awesome/css/font-awesome.css" rel="stylesheet" type="text/css">-->

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

<script type="text/javascript">	
  $(function () {
    $('#chart1').highcharts({
        chart: {
            zoomType: 'x'
        },
        title: {
            text: 'Total Power consumption for April 2014 for Sensor #${sensor}'
        },
        subtitle: {
            text: document.ontouchstart === undefined ?
                    'Click and drag in the plot area to zoom in' :
                    'Pinch the chart to zoom in'
        },
        xAxis: {
            type: 'datetime',
            minRange: 3600000 // 1 hour
        },
        yAxis: {
            title: {
                text: 'Watts'
            }
        },
        legend: {
            enabled: false
        },
        plotOptions: {
         area: {
                fillColor: {
                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                    stops: [
                        [0, Highcharts.getOptions().colors[0]],
                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                    ]
                },
                marker: {
                    radius: 2
                },
                lineWidth: 1,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: null
            }
        },

        series: [{
            type: 'area',
            name: 'Power sensor ${sensor}',
            pointInterval: 3600000, //3600000=1h in millisecs
            pointStart: Date.UTC(2014, 4, 1),
            data: <json:array var="probe" items="${sensorObj.watts}">
        <json:property value="${probe}"/>
</json:array>   }]
    });
});
</script>

</head>

<body onload="responsiveVoice.speak($('#text').val(), 'UK English Female');">
    <div id="wrapper">
        <!-- Navigation -->
        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.jsp">Habit@weave</a>
            </div>
            <!-- Top Menu Items -->
            <ul class="nav navbar-right top-nav">

                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-bell" <c:if test="${indication gt 15000}">style="color:red"</c:if>></i> <b class="caret" <c:if test="${indication gt 15000}">style="color:red"</c:if>></b></a>
                    <ul class="dropdown-menu alert-dropdown">
                        <c:if test="${indication gt 15000}">
                        <li>
                            <a href="#">Hight power consumpiton <span class="label label-warning">${indication} W</span></a>
                        </li>
                        </c:if>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="userpage" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i> ${currentEmail} <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li>
                            <a href="userpage"><i class="fa fa-fw fa-user"></i> Profile</a>
                        </li>
                        <li>
                            <a href="#"><i class="fa fa-fw fa-gear"></i> Settings</a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="logout"><i class="fa fa-fw fa-power-off"></i> Log Out</a>
                        </li>
                    </ul>
                </li>
            </ul>
            <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
            
            
            <div class="collapse navbar-collapse navbar-ex1-collapse">
                <ul class="nav navbar-nav side-nav">
                    <li>
                        <a href="index.jsp"><i class="fa fa-fw fa-desktop"></i> Start</a>
                    </li>
                    <li class="active">
                        <a href="javascript:;" data-toggle="collapse" data-target="#home" aria-expanded="true"><i class="fa fa-fw fa-home"></i> Home appliances <i class="fa fa-fw fa-caret-down"></i></a>
                        <ul id="home" class="collapse in" aria-expanded="true">
                            <li>
                                    <a href="plugwise" class="active"><i class="fa fa-fw fa-plug"></i> Plugwise</a>
                            </li>
                            <li>
                                    <a href="currentcost"><i class="fa fa-fw fa-leaf"></i> Current Cost</a>
                            </li>
                            <li>
                                    <a href="homecreate"><i class="fa fa-fw fa-plus-circle"></i> Add Appliance/Room</a>
                            </li>
                            <li>
                                    <a href="arrangerooms"><i class="fa fa-fw fa-building-o"></i> Arrange in Rooms</a>
                            </li>
                            <li>
                                    <a href="switchplugs"><i class="fa fa-fw fa-power-off"></i> Switch plugs</a>
                            </li>
                            <li>
                                    <a href="schedules"><i class="fa fa-fw fa-calendar"></i> Schedule Appliances</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="javascript:;" data-toggle="collapse" data-target="#health"><i class="fa fa-fw fa-heartbeat"></i> Health appliances <i class="fa fa-fw fa-caret-down"></i></a>
                        <ul id="health" class="collapse">
                            <li>
                                    <a href="activity"><i class="fa fa-fw fa-bicycle"></i> Activity</a>
                            </li>
                            <li>
                                    <a href="sleep"><i class="fa fa-fw fa-bed"></i> Sleep</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="options"><i class="fa fa-fw fa-edit"></i> Options</a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </nav>

        <div id="page-wrapper">

            <div class="container-fluid">

                <!-- Page Heading -->
                <div class="row">
                    <div class="col-lg-12">
                        <h1 class="page-header">
                            <font style = "font-family: 'Gisha'"></font>Habit@<font style = "font-family: 'Harlow'; font-size: 50px">weave</font>
                        </h1>
                        <ol class="breadcrumb">
                            <li>
                                <i class="fa fa-home"></i>  <a href="homeappliances.jsp">Home appliances</a>
                            </li>
                            <li class="active">
                                <i class="fa fa-plug"></i> Plugwise
                            </li>
                        </ol>
                    </div>
                </div>
                <!-- /.row -->
                
                <!-- /.row -->
		<div class="row">
                    <div class="col-lg-12">
                        <div class="panel panel-red">
                            <div class="panel-heading">
                                <h3 class="panel-title">
                                    <i class="fa fa-long-arrow-right"></i> Current consumption of sensor number ${sensor}:</h3>
                            </div>
                            <div class="panel-body">
                                <c:out value="${indication}"></c:out> watts.
                                
                                <div class="hide">
                                    <textarea id="text" cols="45" rows="3">Current consumption of sensor number ${sensor} is <fmt:formatNumber value="${indication}" maxFractionDigits="0" groupingUsed="false"/> watts</textarea>
                                </div>
                                <br>
                                <p><input 
                                        onclick="responsiveVoice.speak($('#text').val(),'UK English Female');" 
                                        type="button" 
                                        value="Read" 
                                    />
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.row -->
                
		<div class="row">
                    <div class="col-lg-5">
                        <div class="panel panel-success">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-plug"></i> Plugwise Appliances</h3>
                            </div>
                            <div class="panel-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover table-striped">
                                        <thead>
                                            <tr>
                                                <th>Vendor name</th>
                                                <th>Description</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="current" items="${sensorIDs}" varStatus="loopCount">
                                            <tr <c:if test="${current eq sensor}">class="warning"</c:if>>
                                                <td><a href="${requestScope["javax.servlet.forward.request_uri"]}?sensor=${current}">
                                                        <c:out value="${current}"></c:out></a></td>
                                                <td>not specified</td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <!--
                                <div class="list-group">
                                        <c:forEach var="current" items="${sensorIDs}">
                                        <a href="${requestScope["javax.servlet.forward.request_uri"]}?sensor=${current}" 
                                            class="list-group-item">PlugWise Adapter #<c:out value="${current}"></c:out></a>
                                        </c:forEach>
                                </div>
                                -->
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-7">
                        <div class="panel panel-yellow">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Consumpion Graph</h3>
                            </div>
                            <div class="panel-body">
                                <div class="flot-chart">

                                    <div id="chart1">Select an available sensor to display chart</div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.row -->
                
                
            </div>
            <!-- /.container-fluid -->

        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->

</body>

</html>
