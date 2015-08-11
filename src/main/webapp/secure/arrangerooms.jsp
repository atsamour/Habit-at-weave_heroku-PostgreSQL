<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Habitatweave: Configure Appliances to Rooms</title>


    <script src="resources/js/jquery.js" ></script>
    <!--<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    -->
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.js"/></script>

<script src="resources/bootstrap/js/bootstrap.min.js"></script> 

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
<!-- vvvvv this one not working..
<link href="resources/font-awesome/css/font-awesome.css" rel="stylesheet" type="text/css">-->

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->

<script type="text/javascript">

    $(document).ready(function () {

        //Sortable and connectable lists (within containment)
        $('#plugSortable .sortable-list').sortable({
            connectWith: '#plugSortable .sortable-list',
            containment: '#containment'
        });

    });
    

</script>

<script type="text/javascript">

    $(document).ready(function () {

        // Get items
        function getItems(divID)
        {
            var finalObj = {};
            
            $(divID + ' ul.sortable-list').each(function () {
                var roomArray = [];
                roomArray = $(this).sortable('toArray');
                finalObj [ $(this).attr('id') ] = roomArray;
            });
            for (var i = 0; i < finalObj.length; i++) {
                console.log(JSON.stringify(finalObj[i]));
            }       
            return finalObj;
        }

        // Get items
        $('#plugSortable .sortable-list').sortable({
            connectWith: '#plugSortable .sortable-list'
        });

        $('#btn-get').click(function () {
           // alert(getItems('#plugSortable'));
                           	
            // Ajax POST request, similar to the GET request.
            $.post('arrangerooms',getItems('#plugSortable'), //url, data e.g { name: "John", time: "2pm" }
                function() { // on success
                    alert("Arrangement saved successfully!");
                })
                .fail(function() { //on failure
                    alert("Arrangement failed.");
                });
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
                                    <a href="arrangerooms" class="active"><i class="fa fa-fw fa-building-o"></i> Arrange in Rooms</a>
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
                                <i class="fa fa-building-o"></i> Arrange in Room
                            </li>
                        </ol>
                    </div>
                </div>
                <!-- /.row -->

                <div class="row">

                    <!-- BEGIN: XHTML for plugSortable -->
                    <div id="plugSortable">
                        <p>
                            <input type="submit" class="input-button" id="btn-get" value="Save rooms"/>
                        </p>
                        <div id="containment">
                            <div class="column left first" style="width: ${width}%">
                                <ul class="sortable-list ui-sortable" id="0" style="background-color:#8ab85c">
                                    Appliances with no room assigned<br><br>
    <c:forEach var="currentAppliance" items="${appliances}"> <c:if test="${currentAppliance.room_id == 0}">
                                    <li class="sortable-item" id="${currentAppliance.id}">${currentAppliance.description}, ID: ${currentAppliance.id}</li>
         </c:if> </c:forEach>
                                </ul>
                            </div>
                            <c:forEach var="currentRoom" items="${rooms}">
                            <div class="column left" style="width: ${width}%">
                                <ul class="sortable-list ui-sortable" id="${currentRoom.id}">
                                    <c:out value="${currentRoom.name}, ID: ${currentRoom.id}"></c:out><br><br>
<c:forEach var="currentAppliance" items="${appliances}"> <c:if test="${currentAppliance.room_id == currentRoom.id}">
                                    <li class="sortable-item" id="${currentAppliance.id}">${currentAppliance.description}, ID: ${currentAppliance.id}</li>
          </c:if></c:forEach>
                                </ul>
                            </div>
                            </c:forEach>
                            <div class="clearer"> </div>
                        </div>
                    </div>
                    <!-- END: XHTML for plugSortable -->

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
