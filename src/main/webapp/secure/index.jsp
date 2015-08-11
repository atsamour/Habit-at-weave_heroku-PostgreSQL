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

    <title>Habitatweave: Home</title>
	
    <script src="resources/js/jquery.js" ></script>
    <script src="resources/bootstrap/js/bootstrap.min.js"></script>
    
    <script src="http://responsivevoice.org/responsivevoice/responsivevoice.js"></script>
	
    <script src="http://code.highcharts.com/highcharts.js"></script>
    <script src="http://code.highcharts.com/modules/data.js"></script>
    <script src="http://code.highcharts.com/modules/heatmap.js"></script>
    <script src="http://code.highcharts.com/modules/exporting.js"></script>
    
    <script type="text/javascript" src="resources/js/moment.js"></script>
    <script type="text/javascript" src="resources/bootstrap/js/transition.js"></script>
    <script type="text/javascript" src="resources/bootstrap/js/collapse.js"></script>
    
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

<pre id="csvMi" style="display: none">Date,Hour,MovingIntensity
<c:forEach var="currentHour" items="${chList}">
${currentHour.date},${currentHour.hour},${currentHour.mi}</c:forEach>
</pre>

<script type="text/javascript">
    
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
            pointInterval: 3600000,
            pointStart: Date.UTC(2014, 3, 1), //or set starting day from DB like at ActivityServlet
            data: <json:array var="probe" items="${sensorObj.watts}">
        <json:property value="${probe}"/>
</json:array>   }]
    });
});


  $(function () {
    $('#chartSleep').highcharts({
        chart: {
             type: 'column'
        },
        title: { <c:set var="length" value="${fn:length(dates)}"/>
            text: 'Sleep statistics from ${dates[0]} to ${dates[length-1]}'
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
    }(Highcharts));


    var start;
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
            text: 'Moving Intensity graph',
            align: 'left',
            x: 40
        },

        subtitle: {<c:set var="length" value="${fn:length(chList)}"/>
            text: 'Moving Intensity per hour from ${chList[0].date} to ${chList[length-1].date}',
            align: 'left',
            x: 40
        },

        xAxis: {
            type: 'datetime',
            min: Date.UTC(2014, 6, 9),
            max: Date.UTC(2014, 6, 28),
            labels: {
                align: 'left',
                x: 5,
                y: 14,
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
            max: 3000,
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
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-bell"></i> <b class="caret"></b></a>
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
                    <li>
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
                        </ol>
                    </div>
                </div>
                <!-- /.row -->
                
                <div class="row">
                    <div class="alert alert-${alert}">
                        <strong><c:out value="${message}"></c:out></strong>
                    </div>  
                       
                </div>
                <!-- /.row -->
                
                <div class="row">
                    <div class="col-lg-12">
                        <div class="panel panel-yellow">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Consumpion Graph</h3>
                            </div>
                            <div class="panel-body">
                                <div class="flot-chart">

                                    <div id="chart1">chart</div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.row -->
                
                <div class="row">
                        <ol class="breadcrumb">
                            <li>
                                <i class="fa fa-heartbeat"></i>  <a href="healthmonitors.jsp">Health monitors</a>
                            </li>
                        </ol>
                    <div class="col-lg-6 ${hide}">
                        <div class="panel panel-green">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Sleeping Graph</h3>
                            </div>
                            <div class="panel-body">
                                <div class="flot-chart">

                                    <div id="chartSleep">sleep chart</div>

                                </div>
                            </div>
                        </div>
                    </div>
                        
                    <div class="col-lg-6 ${hide}">
                        <div class="panel panel-green">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Moving Intensity Graph</h3>
                            </div>
                            <div class="panel-body">
                                <div class="flot-chart">

                                    <div id="chartMi" ></div>

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
