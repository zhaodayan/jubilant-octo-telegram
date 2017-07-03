﻿function FixTable(TableID, FixColumnNumber, width, height) {
		if ($("#" + TableID + "_tableLayout").length != 0) {
			$("#" + TableID + "_tableLayout").before($("#" + TableID));
			$("#" + TableID + "_tableLayout").empty();
		}
		else {
			//if($("#"+TableID).height()<height) height=$("#"+TableID).height();
			$("#" + TableID).after("<div id='" + TableID + "_tableLayout' style='overflow:hidden;height:" + height + "px; width:" + width + "px;'></div>");
		}

		$('<div id="' + TableID + '_tableFix"></div>'
		+ '<div id="' + TableID + '_tableHead"></div>'
		+ '<div id="' + TableID + '_tableColumn"></div>'
		+ '<div id="' + TableID + '_tableData"></div>').appendTo("#" + TableID + "_tableLayout");

		var oldtable = $("#" + TableID);
		
		
		var tableFixClone = oldtable.clone();	
		tableFixClone.attr("id", TableID + "_tableFixClone");		
		$("#" + TableID + "_tableFix").append(tableFixClone);
		tableFixClone.find(".cellData").html("");	
		tableFixClone.width(oldtable.width());		
		
		var tableHeadClone = oldtable.clone();		
		tableHeadClone.attr("id", TableID + "_tableHeadClone");
		$("#" + TableID + "_tableHead").append(tableHeadClone);
		
		tableHeadClone.find(".cellData").html("");
		tableHeadClone.width(oldtable.width());		
		
		var tableColumnClone = oldtable.clone();	
		tableColumnClone.attr("id", TableID + "_tableColumnClone");		
		$("#" + TableID + "_tableColumn").append(tableColumnClone);		
		
		
		tableColumnClone.find(".cellData").html("");
		tableColumnClone.width(oldtable.width());
		
		var Othead = oldtable.children("thead")[0];		
		for(var i=0;i<Othead.rows.length;i++){
			for(var j=0;j<Othead.rows[i].cells.length;j++){
					/*var tth = oldtable.children("thead tr:eq("+i+") th:eq("+j+")");
					var tthc = tableHeadClone.children("thead")[0].rows[i].cells[j];
					var ttcc = tableColumnClone.children("thead")[0].rows[i].cells[j];
					var ttfc = tableFixClone.children("thead")[0].rows[i].cells[j];
					tthc.width=tth.width()+"px";
					ttcc.width=tth.width()+"px";
					ttfc.width=tth.width()+"px";*/
				var tth = oldtable.children("thead").children("tr:eq("+i+")").children("th:eq("+j+")");
				tableHeadClone.children("thead").children("tr:eq("+i+")").children("th:eq("+j+")").width(tth.width());
				tableColumnClone.children("thead").children("tr:eq("+i+")").children("th:eq("+j+")").width(tth.width());
				tableFixClone.children("thead").children("tr:eq("+i+")").children("th:eq("+j+")").width(tth.width());
			}			
		}
		for(var k=0;k<oldtable.children("tbody")[0].rows.length;k++){
			var ttd = oldtable.children("tbody").children("tr:eq("+k+")");					
			var ttcc = tableColumnClone.children("tbody").children("tr:eq("+k+")");//tableColumnClone.children("tbody")[0].rows[k];			
			ttcc.height(ttd.height());		
		}
		
		//$("#" + TableID + "_tableData").append(oldtable);		
		$("#" + TableID + "_tableData")[0].appendChild(oldtable[0]);
		
		
		/*$("#" + TableID + "_tableLayout table").each(function () {
			$(this).css("margin", "0");
		});*/


		var HeadHeight = $("#" + TableID + "_tableHead thead").height();
		HeadHeight += 2;
		$("#" + TableID + "_tableHead").css("height", HeadHeight);
		$("#" + TableID + "_tableFix").css("height", HeadHeight);


		var ColumnsWidth = 0;
		var ColumnsNumber = 0;
		$("#" + TableID + "_tableColumn tr:last td:lt(" + FixColumnNumber + ")").each(function () {
			ColumnsWidth += $(this).outerWidth(true);
			ColumnsNumber++;
		});
		ColumnsWidth += 2;
		if ($.browser.msie) {
			switch ($.browser.version) {
				case "7.0":
					if (ColumnsNumber >= 3) ColumnsWidth--;
					break;
				case "8.0":
					if (ColumnsNumber >= 2) ColumnsWidth--;
					break;
			}
		}
		$("#" + TableID + "_tableColumn").css("width", ColumnsWidth);
		$("#" + TableID + "_tableFix").css("width", ColumnsWidth);		
		
		$("#" + TableID + "_tableData").scroll(function () {
			$("#" + TableID + "_tableHead").scrollLeft($("#" + TableID + "_tableData").scrollLeft());
			$("#" + TableID + "_tableColumn").scrollTop($("#" + TableID + "_tableData").scrollTop());
		});	
		

		$("#" + TableID + "_tableFix").css({ "overflow": "hidden", "position": "relative", "z-index": "50", "background-color": "#f2f2f2" });
		$("#" + TableID + "_tableHead").css({ "overflow": "hidden", "width": width - 17, "position": "relative", "z-index": "45", "background-color": "#f2f2f2" });
		$("#" + TableID + "_tableColumn").css({ "overflow": "hidden", "height": height - 17, "position": "relative", "z-index": "40", "background-color": "#f2f2f2" });
		$("#" + TableID + "_tableData").css({ "overflow": "scroll", "width": width, "height": height, "position": "relative", "z-index": "35" });

		$("#" + TableID + "_tableFix").offset($("#" + TableID + "_tableLayout").offset());
		$("#" + TableID + "_tableHead").offset($("#" + TableID + "_tableLayout").offset());
		$("#" + TableID + "_tableColumn").offset($("#" + TableID + "_tableLayout").offset());
		$("#" + TableID + "_tableData").offset($("#" + TableID + "_tableLayout").offset());

		if($("#"+TableID).width()>width){
			if ($("#" + TableID + "_tableHead").width() > $("#" + TableID + "_tableFix table").width()) {
				$("#" + TableID + "_tableHead").css("width", $("#" + TableID + "_tableFix table").width());
				$("#" + TableID + "_tableData").css("width", $("#" + TableID + "_tableFix table").width() + 17);
			}
		}
		if($("#"+TableID).height()>height){
			if ($("#" + TableID + "_tableColumn").height() > $("#" + TableID + "_tableColumn table").height()) {
				$("#" + TableID + "_tableColumn").css("height", $("#" + TableID + "_tableColumn table").height());
				$("#" + TableID + "_tableData").css("height", $("#" + TableID + "_tableFix table").height() + 17);
			}
		}
	}