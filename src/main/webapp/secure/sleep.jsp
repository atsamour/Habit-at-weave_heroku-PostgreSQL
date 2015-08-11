<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Habitatweave: UP24 Sleeping monitor </title>
	
    <script src="resources/js/jquery.js" ></script>
    <script src="resources/bootstrap/js/bootstrap.min.js"></script>
    
    <script src="http://responsivevoice.org/responsivevoice/responsivevoice.js"></script>
	
    <script src="resources/js/highcharts.js" ></script>
    <!-- For exporting chart as .pdf, e.t.c. -->
    <script src="http://code.highcharts.com/modules/exporting.js"></script>
    
    <script type="text/javascript" src="resources/js/moment.js"></script>
    <script type="text/javascript" src="resources/bootstrap/js/transition.js"></script>
    <script type="text/javascript" src="resources/bootstrap/js/collapse.js"></script>
    <!--Eonasdan bootstrap-datetimepicker -->
    <script type="text/javascript" src="resources/bootstrap/js/bootstrap-datetimepicker.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/numeral.js/1.4.5/numeral.min.js"></script>
    
    
    <!-- Bootstrap -->
    <link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Optional theme -->
    <link rel="stylesheet" href="resources/bootstrap/css/bootstrap-theme.min.css">
    <!-- Custom CSS -->
    <link href="resources/css/sb-admin.css" rel="stylesheet">
    <!-- Custom Fonts -->
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/css/bootstrap-datetimepicker.css" />
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
             type: 'column'
        },
        title: { <c:set var="length" value="${fn:length(dates)}"/>
            text: 'Sleep statistics from ${dates[0]} to ${dates[length-1]} for user ${currentEmail}'
        },
        xAxis: {
            categories: <json:array var="date" items="${dates}">
        <json:property value="${date}"/>
</json:array>
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Sleep time (minutes)'
            },
            stackLabels: {
                enabled: true,
                style: {
                    fontWeight: 'bold',
                    color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                }
            }
        },
        legend: {
            align: 'right',
            x: -30,
            verticalAlign: 'top',
            y: 25,
            floating: true,
            backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
            borderColor: '#CCC',
            borderWidth: 1,
            shadow: false
        },
        tooltip: {
            formatter: function () {
                var minutes = this.y % 60;
                var minutes_string = ((minutes < 10) ? '0' + minutes : minutes);
                return '<b>' + this.x + '</b><br/>' +
                    this.series.name + ': ' + Math.floor(this.y / 60) + ':'+minutes_string+'<br/>' +
                    'Total: ' + this.point.stackTotal;
            }
        },
        plotOptions: {
            column: {
                stacking: 'normal',
                dataLabels: {
                    enabled: true,
                    color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
                    style: {
                        textShadow: '0 0 3px black'
                    }
                }
            }
        },

        series: [{
            name: 'SleepLatency',
            data: <json:array var="TypeMeasurements" items="${measurementsPerType[0]}">
        <json:property value="${TypeMeasurements div 60}"/>
</json:array>
        }, {
            name: 'TotalTimeDeepSleep',
            data: <json:array var="TypeMeasurements" items="${measurementsPerType[1]}">
        <json:property value="${TypeMeasurements div 60}"/>
</json:array>
        }, {
            name: 'TotalTimeInBedButAwake',
            data: <json:array var="TypeMeasurements" items="${measurementsPerType[2]}">
        <json:property value="${TypeMeasurements div 60}"/>
</json:array>
        }, {
            name: 'TotalTimeShallowSleep',
            data: <json:array var="TypeMeasurements" items="${measurementsPerType[3]}">
        <json:property value="${TypeMeasurements div 60}"/>
</json:array>
        }]

    });
});
</script>

</head>

<body>

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
                        <ul id="home" class="collapse">
                            <li>
                                    <a href="plugwise"><i class="fa fa-fw fa-plug"></i> Plugwise</a>
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
                        <ul id="health" class="collapse in" aria-expanded="true">
                            <li>
                                    <a href="activity"><i class="fa fa-fw fa-bicycle"></i> Activity</a>
                            </li>
                            <li>
                                    <a href="sleep" class="active"><i class="fa fa-fw fa-bed"></i> Sleep</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="options"><i class="fa fa-fw fa-edit"></i> Options</a>
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
                                <i class="fa fa-home"></i>  <a href="healthmonitors.jsp">Health monitors</a>
                            </li>
                            <li class="active">
                                <i class="fa fa-bed"></i> Sleep monitoring
                            </li>
                        </ol>
                    </div>
                </div>
                <!-- /.row -->
                
		<div class="row">
                    <div class="col-lg-3">
                        <div class="panel panel-yellow">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-calendar"></i> Select dates</h3>
                            </div>
                            <div class="panel-body">
                                <form role="form" method="post" action="sleep">
                                    <div class="form-group">
                                        <label>Start date</label>
                                        <div class='input-group date' id='datetimepicker6'>
                                            <input type='text' class="form-control" name="date1" />
                                            <span class="input-group-addon">
                                                <span class="glyphicon glyphicon-calendar"></span>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>End date</label>
                                        <div class='input-group date' id='datetimepicker7'>
                                            <input type='text' class="form-control" name="date2" />
                                            <span class="input-group-addon">
                                                <span class="glyphicon glyphicon-calendar"></span>
                                            </span>
                                        </div>
                                    </div>
                                    <script type="text/javascript">
                                        $(function () {
                                            //var mind = new Date("May 21, 2014 00:00:00");
                                            //var maxd = new Date("July 8, 2014 00:00:00");
                                            var mind = new Date("${minD}");
                                            var maxd = new Date("${maxD}");
                                            //var d = new Date();
                                            //options["startDate"] = new Date(d.setDate(d.getDate() - 1));
                                            $('#datetimepicker6').datetimepicker({
                                                format: 'YYYY/MM/DD',
                                                calendarWeeks: true,                            
                                                minDate: mind,               
                                                maxDate: maxd,
                                                //pickTime: false
                                            });
                                            $('#datetimepicker7').datetimepicker({
                                                format: 'YYYY/MM/DD',
                                                calendarWeeks: true,                             
                                                minDate: mind,                     
                                                maxDate: maxd,
                                            });
                                            $("#datetimepicker6").on("dp.change", function (e) {
                                                $('#datetimepicker7').data("DateTimePicker").minDate(e.date);
                                            });
                                            $("#datetimepicker7").on("dp.change", function (e) {
                                                $('#datetimepicker6').data("DateTimePicker").maxDate(e.date);
                                            });
                                        });
                                    </script>
                                 <div class="text-center">
                                        <p><button type="submit" class="btn btn-default">Submit</button></p>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                        
                    <div class="col-lg-9 ${hide}">
                        <div class="panel panel-yellow">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Sleeping Graph</h3>
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
