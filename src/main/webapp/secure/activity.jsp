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

    <title>Habitatweave: UP24 Activity monitor </title>
	
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="resources/bootstrap/js/bootstrap.min.js"></script>
    
    <script src="http://code.highcharts.com/highcharts.js"></script>
    <script src="http://code.highcharts.com/modules/data.js"></script>
    <script src="http://code.highcharts.com/modules/heatmap.js"></script>
    <script src="http://code.highcharts.com/modules/exporting.js"></script>

    
    <script type="text/javascript" src="resources/js/moment.js"></script>
    <script type="text/javascript" src="resources/bootstrap/js/transition.js"></script>
    <script type="text/javascript" src="resources/bootstrap/js/collapse.js"></script>
    <!--Eonasdan bootstrap-datetimepicker -->
    <script type="text/javascript" src="resources/bootstrap/js/bootstrap-datetimepicker.min.js"></script>

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


<pre id="csv" style="display: none">Date,Hour,Calories
<c:forEach var="currentHour" items="${chList}">
${currentHour.date},${currentHour.hour},${currentHour.calories}</c:forEach>
</pre>

<pre id="csvMi" style="display: none">Date,Hour,MovingIntensity
<c:forEach var="currentHour" items="${chList}">
${currentHour.date},${currentHour.hour},${currentHour.mi}</c:forEach>
</pre>

<script type="text/javascript">	
$(function () {

    /**
     * This plugin extends Highcharts in two ways:
     * - Use HTML5 canvas instead of SVG for rendering of the heatmap squares. Canvas
     *   outperforms SVG when it comes to thousands of single shapes.
     * - Add a K-D-tree to find the nearest point on mouse move. Since we no longer have SVG shapes
     *   to capture mouseovers, we need another way of detecting hover points for the tooltip.
     */
    (function (H) {
        var Series = H.Series,
            each = H.each,
            wrap = H.wrap,
            seriesTypes = H.seriesTypes;

        /**
         * Create a hidden canvas to draw the graph on. The contents is later copied over 
         * to an SVG image element.
         */
        Series.prototype.getContext = function () {
            if (!this.canvas) {
                this.canvas = document.createElement('canvas');
                this.canvas.setAttribute('width', this.chart.chartWidth);
                this.canvas.setAttribute('height', this.chart.chartHeight);
                this.image = this.chart.renderer.image('', 0, 0, this.chart.chartWidth, this.chart.chartHeight).add(this.group);
                this.ctx = this.canvas.getContext('2d');
            }
            return this.ctx;
        };

        /** 
         * Draw the canvas image inside an SVG image
         */ 
        Series.prototype.canvasToSVG = function () {
            this.image.attr({ href: this.canvas.toDataURL('image/png') });
        };

        /**
         * Wrap the drawPoints method to draw the points in canvas instead of the slower SVG,
         * that requires one shape each point.
         */
        H.wrap(H.seriesTypes.heatmap.prototype, 'drawPoints', function (proceed) {

            var ctx = this.getContext();
            
            if (ctx) {

                // draw the columns
                each(this.points, function (point) {
                    var plotY = point.plotY,
                        shapeArgs;

                    if (plotY !== undefined && !isNaN(plotY) && point.y !== null) {
                        shapeArgs = point.shapeArgs;

                        ctx.fillStyle = point.pointAttr[''].fill;
                        ctx.fillRect(shapeArgs.x, shapeArgs.y, shapeArgs.width, shapeArgs.height);
                    }
                });

                this.canvasToSVG();

            } else {
                this.chart.showLoading("Your browser doesn't support HTML5 canvas, <br>please use a modern browser");

                // Uncomment this to provide low-level (slow) support in oldIE. It will cause script errors on
                // charts with more than a few thousand points.
                //proceed.call(this);
            }
        });
        H.seriesTypes.heatmap.prototype.directTouch = false; // Use k-d-tree
    }(Highcharts));


    var start;
    $('#chart1').highcharts({

        data: {
            csv: document.getElementById('csv').innerHTML,
            parsed: function () {
                start = +new Date();
            }
        },

        chart: {
            type: 'heatmap',
            margin: [60, 10, 80, 50]
        },


        title: {
            text: 'Calories consumption graph for user ${currentEmail}',
            align: 'left',
            x: 40
        },

        subtitle: {<c:set var="length" value="${fn:length(chList)}"/>
            text: 'Calories per hour from ${chList[0].date} to ${chList[length-1].date}',
            align: 'left',
            x: 40
        },

        xAxis: {
            type: 'datetime',
            min: Date.UTC(${minDate}),
            max: Date.UTC(${maxDate}),
            labels: {
                align: 'left',
                x: 5,
                y: 8,
                //format: '{value:%B}' // long month
            },
            showLastLabel: false,
            tickLength: 2
        },

        yAxis: {
            title: {
                text: null
            },
            labels: {
                format: '{value}:00'
            },
            minPadding: 0,
            maxPadding: 0,
            startOnTick: false,
            endOnTick: false,
            tickPositions: [0, 6, 12, 18, 24],
            tickWidth: 1,
            min: 0,
            max: 23,
            reversed: true
        },

        colorAxis: {
            stops: [
                [0, '#f3d8d6'], //#3060cf blue
                [0.1, '#e7b2af'],
                [0.5, '#d06a60'], //#fffbbc light yellow
                [0.9, '#752a23'], //#c4463a mpornto
                [1, '#3a1511'] //#c4463a mpornto
            ],
            min: 0,
            max: 65,
            startOnTick: false,
            endOnTick: false,
            labels: {
                format: '{value} cal'
            }
        },

        series: [{
            borderWidth: 0,
            nullColor: '#EFEFEF',
            colsize: 24 * 36e5, // one day
            tooltip: {
                headerFormat: 'Calories<br/>',
                pointFormat: '{point.x:%e %b, %Y} {point.y}:00: <b>{point.value}</b>'
            },
            turboThreshold: Number.MAX_VALUE // #3404, remove after 4.0.5 release
        }]

    });
    
    
    $('#chartMi').highcharts({

        data: {
            csv: document.getElementById('csvMi').innerHTML,
            parsed: function () {
                start = +new Date();
            }
        },

        chart: {
            type: 'heatmap',
            margin: [60, 10, 80, 50]
        },


        title: {
            text: 'Moving Intensity graph  for user ${currentEmail}',
            align: 'left',
            x: 40
        },

        subtitle: {
            text: 'Moving Intensity per hour from ${chList[0].date} to ${chList[length-1].date}',
            align: 'left',
            x: 40
        },

        xAxis: {
            type: 'datetime',
            min: Date.UTC(${minDate}),
            max: Date.UTC(${maxDate}),
            labels: {
                align: 'left',
                x: 5,
                y: 8,
                //format: '{value:%B}' // long month
            },
            showLastLabel: false,
            tickLength: 2
        },

        yAxis: {
            title: {
                text: null
            },
            labels: {
                format: '{value}:00'
            },
            minPadding: 0,
            maxPadding: 0,
            startOnTick: false,
            endOnTick: false,
            tickPositions: [0, 6, 12, 18, 24],
            tickWidth: 1,
            min: 0,
            max: 23,
            reversed: true
        },

        colorAxis: {
            stops: [
                [0, '#d6f3e7'], //#3060cf blue
                [0.1, '#afe7ce'],
                [0.5, '#60d0a2'], //#fffbbc light yellow
                [0.9, '#237553'], //#c4463a mpornto
                [1, '#113a2a'] //#c4463a mpornto
            ],
            min: 0,
            max: 3000,//?????
            startOnTick: false,
            endOnTick: false,
            labels: {
                format: '{value} mi'
            }
        },

        series: [{
            borderWidth: 0,
            nullColor: '#EFEFEF',
            colsize: 24 * 36e5, // one day
            tooltip: {
                headerFormat: 'Moving Intensity<br/>',
                pointFormat: '{point.x:%e %b, %Y} {point.y}:00: <b>{point.value}</b>'
            },
            turboThreshold: Number.MAX_VALUE // #3404, remove after 4.0.5 release
        }]

    });
    
    
    console.log('Rendered in ' + (new Date() - start) + ' ms');

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
                                    <a href="activity" class="active"><i class="fa fa-fw fa-bicycle"></i> Activity</a>
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
                                <i class="fa fa-home"></i>  <a href="healthmonitors.jsp">Health monitors</a>
                            </li>
                            <li class="active">
                                <i class="fa fa-bicycle"></i> Calories monitoring
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
                                <form role="form" method="post" action="activity">
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
                                            //var mind = new Date("July 9, 2014 00:00:00");
                                            //var maxd = new Date("December 1, 2014 00:00:00");
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
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Calories Graph</h3>
                            </div>
                            <div class="panel-body">
                                <div class="flot-chart">

                                    <div id="chart1" style="height: 320px; width: 700px; margin: 0 auto"></div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.row -->
                
                <div class="row">
                    <div class="col-lg-3">
                        
                    </div>
                        
                    <div class="col-lg-9 ${hide}">
                        <div class="panel panel-green">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Moving Intensity Graph</h3>
                            </div>
                            <div class="panel-body">
                                <div class="flot-chart">

                                    <div id="chartMi" style="height: 320px; width: 700px; margin: 0 auto"></div>

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
