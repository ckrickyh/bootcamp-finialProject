// Assuming stocks data is provided from Thymeleaf
// var stocks = /*[[${(stocks)}]]*/[];

// Convert stocks data to the format required by ECharts
var chartData = [];
var industryMap = {};
var x = updateChart(stocks); // Get chart data

function updateChart(stocks) {
    const totalPercentageChange = calculateTotalPercentageChange(stocks);
    // ========= assume ===================
    // const stocks = [
    //     { code: 'AAPL', finnhubIndustry: 'Technology', percentageChange: 5 },
    //     { code: 'MSFT', finnhubIndustry: 'Technology', percentageChange: -2 },
    //     { code: 'XOM', finnhubIndustry: 'Energy', percentageChange: 3 },
    //     { code: 'CVX', finnhubIndustry: 'Energy', percentageChange: 1 }
    // ];
    //==============================================
    // {
    //     Technology: [
    //         { name: 'AAPL', value: 5 },
    //         { name: 'MSFT', value: -2 }
    //     ],
    //     Energy: [
    //         { name: 'XOM', value: 3 },
    //         { name: 'CVX', value: 1 }
    //     ]
    // }
    chartData = [];
    industryMap = {};

    stocks.forEach(stock => {
        if (!industryMap[stock.finnhubIndustry]) {
            industryMap[stock.finnhubIndustry] = [];
        }
        industryMap[stock.finnhubIndustry].push({ //.js push ,add數組item , 
            name: stock.code,
            value: Math.abs(Number(stock.percentageChange)), //一定要abs, negative not approve
            itemStyle: {
                color: getColor(stock.percentageChange) // Apply color based on percentageChange
            },
            label: {
                // fontSize: getDynamicFontSize(stock.percentageChange), // Dynamic font size
                fontSize: getFontSizeByPercentageChange(stock.percentageChange, totalPercentageChange), // Set font size based on percentage of total
                shadowColor: "rgba(201, 174, 74, 1)",
                shadowBlur: 3.1,
                shadowOffsetX: 4,
                shadowOffsetY: 1
            },
            sign: stock.percentageChange,
            open: stock.open,
            close: stock.previousClose,
            currentPrice: stock.currentPrice
        });
    });

    //===================chartData=====================
    // [
    //     {name : Technology      
    //     children : [ 
    //         {name : AAPL, value : 23.4},
    //         {name : googl, value : 23.5}
    //     ]},
    //     {name : Sales
    //     children : [
    //          {name : tsla, value : 23.4},
    //          {name : yahoo, value : 23.5}
    //     ]}
    // }
    for (var industry in industryMap){
        chartData.push({
            name: industry,
            children: industryMap[industry]
        });
    }
    return chartData;
}

// ================= option =================
// Specify the initial configuration for the treemap
let option = {
    tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(50,50,50,0.7)',
        formatter: function (params) {
            if (params && params.data && params.data.currentPrice != null) {
                const sign = params.data.sign > 0 ? '▲' : params.data.sign < 0 ? '▼' : '-';
                const code = encodeURIComponent(params.name);
                const url = 'http://localhost:8102/us/historystick/' + code; // wrong http://localhost:8092/us/historystick/' + code; correct 'http://localhost:8102/us/historystick/' + code;

                return `
                    <div style="background: rgba(0, 0, 0, 0.1); padding: 10px; border-radius: 5px;">
                        Current Price: ${params.data.currentPrice}<br>
                        Open: ${params.data.open}           Close: ${params.data.close}<br>
                        <iframe src="${url}" width="400" height="200" frameborder="0" scrolling="no"></iframe>
                    </div>
                `;
            }
            return `
                <div style="background: rgba(0, 0, 0, 0.1); padding: 10px; border-radius: 5px;">
                    Section ${params.name}<br>
                </div>
            `;
        },
        alwaysShowContent: false // Ensure the tooltip is always shown
    },
    series: [{
        type: 'treemap',

        // var stocks = /*[[${(stocks)}]]*/[];
        data: x,

        label: {
            show: true,
            formatter: function(params) {
                console.log("process params" + params.name);
                var value = Number(params.value);
                console.log("process " + value);
                var sign = params.data.sign > 0 ? '▲' : params.data.sign < 0 ? '▼' : '-';
                return `${params.name}\n${sign} ${(params.value).toFixed(3)}%`; // Add sign here
            },
            textShadowColor: "rgba(224, 223, 223, 0.8)",
            textShadowBlur: 25,
            textShadowOffsetX: 4,
            textShadowOffsetY: 4,
            position: 'inside'
            // overflow: "break",
        },
        upperLabel: {
            show: true,
            height: 30,
            formatter: '{b}', // Displaying the finnhubIndustry
            color: '#fff', // Text color
            backgroundColor: '#000', // Background color
            offset: [2, 1],
            fontFamily: "serif",
        },
        itemStyle: {
            normal: {
                borderColor: '#121212ff',
                borderWidth: 1,
            }
        },
        roam: true, // Enable zooming and panning
        emphasis: {focus: 'ancestor', blurScope: 'coordinateSystem'},
        breadcrumb: {show: true, itemStyle: {color: '#8e8e90ff',fontSize: 14}}
    }],     
    //=== end series
};

// ===================================

// Color Setting
function getColor(change) {
    if (change === null || change === undefined) return '#666';
    change = parseFloat(change);
    if (isNaN(change)) return '#666';
    if (change > 2) return '#088d4fff';    // strong green
    if (change > 1) return '#21db65';      // light green
    if (change > 0) return '#3ce899';      // pale green
    if (change === 0) return '#e5e7eb';    // neutral gray
    if (change > -1) return '#f87171';     // pale red
    if (change > -2) return '#ed3737';     // red
    return '#db0909';                      // strong red
}

// Function to dynamically determine font size based on percentageChange
// function getDynamicFontSize(percentageChange) {
//     let absChange = Math.abs(percentageChange);
//     if (absChange > 2) return 40; // Large font for high changes
//     if (absChange > 1.5) return 30;
//     if (absChange > 1) return 15; 
//     if (absChange > 0.5) return 10;
//     return 10; // Small font for low changes
// };

// Function to calculate total percentage change
function calculateTotalPercentageChange(stocks) {
    return stocks.reduce((total, stock) => total + Math.abs(stock.percentageChange), 0);
}

// Function to determine font size based on percentage of total change
function getFontSizeByPercentageChange(percentageChange, totalChange) {
    const percentage = (Math.abs(percentageChange) / totalChange) * 100;

    if (percentage > 20) return 40; // Large font for significant changes
    if (percentage > 10) return 30;  // Medium font
    if (percentage > 5) return 20;   // Small font
    return 10;                       // Very small font
}

// ======================Main==================

// Initialize the ECharts instance
var myChart = echarts.init(document.getElementById('main-heatmap'));

var x = updateChart(stocks);
console.log(x);
myChart.setOption(option);

myChart.on('dblclick', function(params) {
    const code = params.name.split(" ")[0];
    // window.open('http://localhost:8092/us/historystick/' + encodeURIComponent(code), "code", "location=0,width=600,height=400").focus();
    window.open('http://localhost:8102/us/historystick/' + encodeURIComponent(code), "code", "location=0,width=600,height=400").focus();
    // not ok http://ui-app:8102/us/historystick/'

    // window.open("url","winName","location=0,width=300,height=214").focus();
});

// Responsive
window.addEventListener('resize', function () {
    myChart.resize();
});

// Refresh each 25s
function refresh() {
    $.ajax({
        url: "/us/realtime", // Your endpoint to fetch the updated coin data
        type: "GET",
        success: function(data) {
            stocks = data; // Update the stocks variable with new data
            updateChart(stocks); // Call the function to update the chart with new data
            console.log("Updated stocks data:", stocks);

        myChart.setOption({
            series: [{
                data: chartData // Pass the updated chartData
            }]
        });
        console.log("Updated stocks data:", stocks);
        },
        error: function(err) {
            console.error("Error fetching data:", err);
        }
    });
}

// jQuery
$(document).ready(function () {
    refresh(); // Initial call to fetch data
    setInterval(refresh, 60000); // Update every 25 seconds
});

