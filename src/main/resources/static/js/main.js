            // process the data from the server 
            // to return a dictionary than can be used for makeChart
            const process_chart_data = (array_1, array_2) => {
                let chart_data = {chartData:[], labels:[]}
                let i = 0;
                while(i < array_2.length){
                    let cur_val = array_2[i];
                    let count = count_consec_occur(array_2, i, cur_val);
                    let sum = sum_fr_to(array_1, i, i+count);
                    chart_data["chartData"].push(sum);
                    chart_data["labels"].push(cur_val)
                    i += count;
                } 
                return chart_data;
            }
            const count_consec_occur = (array, start_index, element) => {
                let occurence_count = 0;
                for(let i = start_index; i < array.length; i++){
                   var cur_val = array[i];
                    if(cur_val == element){ occurence_count++ }
                    else{ break; }
                }
                return occurence_count;
            }
            const sum_fr_to = (array, start_index, end_index) => {
                let mySum = 0;
                for(let i = start_index; i < end_index; i++){
                    mySum += array[i];
                }
                return mySum
            }
(function ($) {
    "use strict";

    // Spinner
    var spinner = function () {
        setTimeout(function () {
            if ($('#spinner').length > 0) {
                $('#spinner').removeClass('show');
            }
        }, 1);
    };
    spinner();
    
    
    // Back to top button
    $(window).scroll(function () {
        if ($(this).scrollTop() > 300) {
            $('.back-to-top').fadeIn('slow');
        } else {
            $('.back-to-top').fadeOut('slow');
        }
    });
    $('.back-to-top').click(function () {
        $('html, body').animate({scrollTop: 0}, 1500, 'easeInOutExpo');
        return false;
    });


    // Sidebar Toggler
    $('.sidebar-toggler').click(function () {
        $('.sidebar, .content').toggleClass("open");
        return false;
    });


    // Progress Bar
    $('.pg-bar').waypoint(function () {
        $('.progress .progress-bar').each(function () {
            $(this).css("width", $(this).attr("aria-valuenow") + '%');
        });
    }, {offset: '80%'});


    // Calender
    $('#calender').datetimepicker({
        inline: true,
        format: 'L'
    });


    // Testimonials carousel
    $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1000,
        items: 1,
        dots: true,
        loop: true,
        nav : false
    });


    // Chart Global Color
    Chart.defaults.color = "#6C7293";
    Chart.defaults.borderColor = "#000000";


    // Worldwide Sales Chart
    var ctx1 = $("#worldwide-sales").get(0).getContext("2d");
    var totalExpense =document.getElementById("totalExpense").textContent;
    var allocatedBalance =document.getElementById("allocatedBalance").textContent;
    var unallocatedBalance =document.getElementById("unallocatedBalance").textContent;
    var myChart1 = new Chart(ctx1, {
        type: "bar",
        data: {
            labels: ["May"],
            datasets: [{
                    label: "Expences",
                    data: [totalExpense],
                    backgroundColor: "rgba(235, 22, 22, .7)"
                },
                {
                    label: "Allocated",
                    data: [allocatedBalance],
                    backgroundColor: "rgba(235, 22, 22, .5)"
                },
                {
                    label: "UnAllocated",
                    data: [unallocatedBalance],
                    backgroundColor: "rgba(235, 22, 22, .3)"
                }
            ]
            },
        options: {
            responsive: true
        }
    });


    // Salse & Revenue Chart
//
var ctx2 = $("#salse-revenue").get(0).getContext("2d");
let expenseData = [];
let incomeData = [];

fetch('/users/api/expense/statsmonth')
  .then(response => response.json())
  .then(rawExpenses => {
    console.log(rawExpenses);
    let { chartData: actualData, labels } = process_chart_data(
      rawExpenses.map(data => data.price),
      rawExpenses.map(data => data.creationTime.slice(5, 10))
    );
    console.log(actualData);
    console.log(labels);
    console.log(rawExpenses.map(data => data.price));

    fetch('/users/api/income/statsmonth')
      .then(response => response.json())
      .then(rawIncome => {
        console.log(rawIncome);
        let { chartData: actualData2, labels2 } = process_chart_data(
          rawIncome.map(data => data.amount),
          rawIncome.map(data => data.creationTime.slice(5, 10))
        );
        console.log(actualData2);
        console.log(labels2);
        console.log(rawIncome.map(data => data.Income));

        var myChart2 = new Chart(ctx2, {
          type: "line",
          data: {
            labels: labels,
            datasets: [
              {
                label: "Expenses",
                data: actualData,
                backgroundColor: "rgba(235, 22, 22, .7)",
                fill: true
              },
              {
                label: "Income",
                data: actualData2,
                backgroundColor: "rgba(235, 22, 22, .5)",
                fill: true
              }
            ]
          },
          options: {
            responsive: true
          }
        });
      })
      .catch(error => {
        console.error('Error:', error);
        // Handle error if necessary
      });
  })
  .catch(error => {
    console.error('Error:', error);
    // Handle error if necessary
  });
    


//    // Single Line Chart
//    var ctx3 = $("#line-chart").get(0).getContext("2d");
//    var myChart3 = new Chart(ctx3, {
//        type: "line",
//        data: {
//            labels: [50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150],
//            datasets: [{
//                label: "Salse",
//                fill: false,
//                backgroundColor: "rgba(235, 22, 22, .7)",
//                data: [7, 8, 8, 9, 9, 9, 10, 11, 14, 14, 15]
//            }]
//        },
//        options: {
//            responsive: true
//        }
//    });
//
//
//    // Single Bar Chart
//    var ctx4 = $("#bar-chart").get(0).getContext("2d");
//    var myChart4 = new Chart(ctx4, {
//        type: "bar",
//        data: {
//            labels: ["Italy", "France", "Spain", "USA", "Argentina"],
//            datasets: [{
//                backgroundColor: [
//                    "rgba(235, 22, 22, .7)",
//                    "rgba(235, 22, 22, .6)",
//                    "rgba(235, 22, 22, .5)",
//                    "rgba(235, 22, 22, .4)",
//                    "rgba(235, 22, 22, .3)"
//                ],
//                data: [55, 49, 44, 24, 15]
//            }]
//        },
//        options: {
//            responsive: true
//        }
//    });
//
//
//    // Pie Chart
//    var ctx5 = $("#pie-chart").get(0).getContext("2d");
//    var myChart5 = new Chart(ctx5, {
//        type: "pie",
//        data: {
//            labels: ["Italy", "France", "Spain", "USA", "Argentina"],
//            datasets: [{
//                backgroundColor: [
//                    "rgba(235, 22, 22, .7)",
//                    "rgba(235, 22, 22, .6)",
//                    "rgba(235, 22, 22, .5)",
//                    "rgba(235, 22, 22, .4)",
//                    "rgba(235, 22, 22, .3)"
//                ],
//                data: [55, 49, 44, 24, 15]
//            }]
//        },
//        options: {
//            responsive: true
//        }
//    });
//
//
//    // Doughnut Chart
//    var ctx6 = $("#doughnut-chart").get(0).getContext("2d");
//    var myChart6 = new Chart(ctx6, {
//        type: "doughnut",
//        data: {
//            labels: ["Italy", "France", "Spain", "USA", "Argentina"],
//            datasets: [{
//                backgroundColor: [
//                    "rgba(235, 22, 22, .7)",
//                    "rgba(235, 22, 22, .6)",
//                    "rgba(235, 22, 22, .5)",
//                    "rgba(235, 22, 22, .4)",
//                    "rgba(235, 22, 22, .3)"
//                ],
//                data: [55, 49, 44, 24, 15]
//            }]
//        },
//        options: {
//            responsive: true
//        }
//    });

    
})(jQuery);

