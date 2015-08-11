<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<!DOCTYPE html>
<html data-ng-app lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Habitatweave: Schedule works</title>


<script src="resources/js/jquery.js" ></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.js"/></script>

<script type="text/javascript" src="https://code.angularjs.org/1.4.3/angular.js"/></script>


<script src="resources/bootstrap/js/bootstrap.min.js"></script> 

<script type="text/javascript" src="resources/js/moment.js"></script>
<script type="text/javascript" src="resources/bootstrap/js/transition.js"></script>
<script type="text/javascript" src="resources/bootstrap/js/collapse.js"></script>
<script type="text/javascript" src="resources/bootstrap/js/bootstrap-datetimepicker.min.js"></script>

<!-- Bootstrap -->
<link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<!-- Optional theme -->
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap-theme.min.css">
<!-- Custom CSS -->
<link href="resources/css/sb-admin.css" rel="stylesheet">
<!-- Custom Fonts -->
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
<!-- Custom rooms CSS -->
<link href="resources/css/rooms.css" rel="stylesheet">

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/css/bootstrap-datetimepicker.css" />

<!--<link rel="stylesheet" href="resources/bootstrap/css/bootstrap-datetimepicker.min.css" />
 vvvvv this one not working..
<link href="resources/font-awesome/css/font-awesome.css" rel="stylesheet" type="text/css">-->

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->

<style TYPE="text/css" media="all">
    
    .ui-autocomplete {
    position: absolute;
    z-index: 1000;
    //cursor: default;
    
    cursor:pointer; 
    height:120px; 
    overflow-y:scroll;
//height: 200px; 
    //overflow-y: scroll; 
    //overflow-x: hidden;

    padding: 0;
    margin-top: 2px;
    list-style: none;
    background-color: #ffffff;
    border: 1px solid #ccc;
    -webkit-border-radius: 5px;
       -moz-border-radius: 5px;
            border-radius: 5px;
    -webkit-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
       -moz-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
            box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
    }
    .ui-autocomplete > li {
      padding: 3px 20px;
    }
    .ui-autocomplete > li.ui-state-focus {
      background-color: #DDD;
    }
    .ui-helper-hidden-accessible {
      display: none;
    }

</style>

<script type="text/javascript">
$(document).ready(function() {
    $(function() {
        $("input#appl").autocomplete({
            source: function(request, response) {
                $.ajax({
                    url: "${requestScope["javax.servlet.forward.context_path"]}/AutoCompleteResponce",
                    type: "GET",
                    data: { term: request.term },

                    dataType: "json",

                    success: function(data) {
                        console.log( data);
                        response(data);
                    }
               });              
            },
            focus: function( event, ui ) {
              $( "#appl" ).val( ui.item.label );
              return false;
            },
            select: function( event, ui ) {
              $( "#appl" ).val( ui.item.label );
              $( "#appl-id" ).val( ui.item.value );
              $( "#appl-description" ).html( ui.item.desc );
             
              return false;
            },
            minLength: 0,
            scroll: true
        })
        
        .focus(function() {
            $(this).autocomplete("search", "");
        })
        
        .autocomplete( "instance" )._renderItem = function( ul, item ) {
            return $( "<li>" )
                .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
                .appendTo( ul );
        };
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
                    <li class="active">
                        <a href="javascript:;" data-toggle="collapse" data-target="#home" aria-expanded="true"><i class="fa fa-fw fa-home"></i> Home appliances <i class="fa fa-fw fa-caret-down"></i></a>
                        <ul id="home" class="collapse in" aria-expanded="true">
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
                                    <a href="schedules" class="active"><i class="fa fa-fw fa-calendar"></i> Schedule Appliances</a>
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
                            <li class="active">
                                <i class="fa fa-building-o"></i> Schedule Appliances
                            </li>
                        </ol>
                    </div>
                </div>
                <!-- /.row -->
                <h2 style="color:red">    
                    <c:out value="${message}"></c:out>
                </h2>

                <div class="row">
                        
                </div>
                <!-- /.row -->
                
                <div class="row">                    
                    <div class="col-lg-6">
                        <div class="panel panel-yellow">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-calendar"></i> New schedule</h3>
                            </div>
                            <div class="panel-body">
                                <form role="form" method="post" action="schedules">
                                    <div class="form-group">
                                        <label>Schedule description</label>
                                        <input class="form-control" name="description" placeholder="e.g Hall lights on">
                                        <br/><input type="text" data-ng-model="name"/> {{name}}
                                    </div>
                                   <!-- <div class="form-group">
                                        <label>Select appliance</label>
                <c:forEach var="currentAppliance" items="${appliances}">
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="appliance" id="optionsRadios${currentAppliance.id}" value="${currentAppliance.id}">
                                                ${currentAppliance.description}, ${currentAppliance.vendorname}
                                            </label>
                                        </div>
                </c:forEach>               
                                    </div>
                                    -->
                                    <div class="form-group">
                                        <label>Select appliance</label>
                                        <input class="form-control" id="appl" placeholder="Enter Appliance description" />
                                        <input type="hidden" name="appliance" id="appl-id">
                                    </div>    
                                    
                                    <div class="form-group">
                                        <label>Action</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="action" id="optionsRadios1" value="ON"> <i class="fa fa-toggle-on"></i> ON
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="action" id="optionsRadios2" value="OFF"> <i class="fa fa-toggle-off"></i> OFF
                                            </label>
                                        </div>
                                    </div>  
                                    
                                    <div class="form-group">
                                        <label>Select date+time</label>
                                        <div class='input-group date' id='datetimepicker1'>
                                            <input type='text' class="form-control" name="date" />
                                            <span class="input-group-addon">
                                                <span class="glyphicon glyphicon-calendar"></span>
                                            </span>
                                        </div>
                                    </div>
                                    
                                <script type="text/javascript">
                                    $(function () {
                                        var mind = new Date(Date.now());  
                                        $('#datetimepicker1').datetimepicker({
                                            format: 'YYYY/MM/DD HH:mm',                       
                                            minDate: mind 
                                            //weekStart: '1'
                                            //pickSeconds: false
                                            //pick12HourFormat: false
                                            //startDate: "today"
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
                    <div class="col-lg-6">
                    
                        <div class="panel panel-green">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Pending Works</h3>
                            </div>
                            <div class="panel-body">
                                <div class="list-group">
                                        <c:forEach var="current" items="${workToDoList}">
                                            Work description: <c:out value="${current.description}"></c:out><br>
                                        </c:forEach>
                                </div>
                            </div>
                        </div>
                        <div class="panel panel-red">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Old Works</h3>
                            </div>
                            <div class="panel-body">
                                <div class="list-group">
                                        <c:forEach var="current" items="${workOldList}">
                                            Work description: <c:out value="${current.description}"></c:out><br>
                                        </c:forEach>
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
