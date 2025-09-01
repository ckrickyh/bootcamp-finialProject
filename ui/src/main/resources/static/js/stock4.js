
// let stocks = /*[[${(stocks)}]]*/[];
function getHeatmapColor(change) {
    if (change === null || change === undefined) return '#666';
    change = parseFloat(change);
    if (isNaN(change)) return '#666';
    if (change > 2) return '#08bf6b';    // strong green
    if (change > 1) return '#21db65';    // light green
    if (change > 0) return '#3ce899';    // pale green
    if (change === 0) return '#e5e7eb';  // neutral gray
    if (change > -1) return '#f87171';   // pale red
    if (change > -2) return '#ed3737';   // red
    return '#db0909';                    // strong red
};

// data
// ======================= data ===========================
// Thymeleaf inline JS to pass stock data
// data:  stocks = /*[[${(stocks)}]]*/[] <= ResultFinnhubDTO

// example
// var names = stocks.map(stock => stock.name); // Assuming each stock has a 'name'
// var salesData = stocks.map(stock => stock.sales); // Assuming each stock has a 'sales' value

// treemapData : Setting calling attribute by entity(code), entity(perecentageChange), entity(volume)...
let treemapData = stocks.map(stock => ({
    // value: stock.code + ' (' + (stock.percentageChange > 0 ? '+' : '') + stock.percentageChange + '%)',
    name: stock.code,
    value: Math.abs(stock.percentageChange),
    upDown: stock.percentageChange > 0 ? '▲' : stock.percentageChange < 0 ? '▼' : '-',
    currentPrice: stock.currentPrice,
    high: stock.high,
    low: stock.low,
    code: stock.code,
    logoLink: stock.logoLink,
    open: stock.open,
    close: stock.previousClose,
    itemStyle: {
        color: getHeatmapColor(stock.percentageChange)
    },
}));
    // "code": "BBAI",
    // "currentPrice": 7.55,
    // "changePrice": 0.18,
    // "percentageChange": 2.4423,
    // "high": 7.82,
    // "low": 7.42,
    // "open": 7.49,
    // "previousClose": 7.37,
    // "timestamp": 1753287656,
    // "finnhubIndustry": "Technology"

// ECharts option , jsStock injection
let option = {
        series: [{
            type: 'treemap',
            layoutAlgorithm: 'squarified',
            data: treemapData, // !!!  let treemapData = stocks.map(stock => ({ .....
            label: {
                show: true,
                // formatter: '{b}\n\n{c}',
                position: 'inside',

                // =======formatter
                formatter: function(params) {
                    const percenValue = params.value;
                    let fontSizeStyle;

                    // Determine font size style based on open value
                    if (percenValue <= 0.3) {
                        fontSizeStyle = 'nameStyleSmall';
                    } else if (percenValue <= 2) {
                        fontSizeStyle = 'nameStyleMedium';
                    } else {
                        fontSizeStyle = 'nameStyleLarge';
                    }

                    return [
                        `{${fontSizeStyle}| }`,
                        `{${fontSizeStyle}|${params.name}}`,
                        `{upDownStyle|${params.data.upDown}} ${'{valueStyle|' + params.value + '%}'}`
                    ].join('\n');
                  },
                // end formatter========

                //====rich
                //https://apache.github.io/echarts-handbook/en/how-to/label/rich-text/
                rich: {
                    nameStyleSmall: {
                          fontSize: 7,
                          color: '#fff',
                          fontWeight: 'bold',
                          align: 'center',
                          position: 'inside'
                    },
                    nameStyleMedium: {
                          fontSize: 30,
                          color: '#fff',
                          fontWeight: 'bold',
                          align: 'center',
                          position: 'inside'
                    },
                    nameStyleLarge: {
                          fontSize: 50,
                          color: '#fff',
                          fontWeight: 'bold',
                          align: 'center',
                          position: 'inside'
                    },
                    valueStyle: {
                        fontSize: 20,
                        color: '#fff',
                        align: 'center',
                        position: 'inside'
                    },
                    upDownStyle: {
                        fontSize: 20,
                        color: '#fff',
                        align: 'center',
                        position: 'inside',
                    }
                }
                //=========end rich
            },
            // =====end label
            itemStyle: {
                borderColor: 'black', // Black border
                borderWidth: 1
            },
            //======end itemStyle
            levels: [
                {
                    itemStyle: {
                        borderWidth: 2,
                        borderColor: '#333',
                        gapWidth: 1
                    }
                },
                {
                    color: ['#942e38', '#aaa', '#269f3c'],
                    colorMappingBy: 'value',
                    itemStyle: {
                        gapWidth: 1
                    }
                }
            ],
            //====end levels
            roam: true,
            breadcrumb: { show: true },
        }],
        //=== end series
        left: "10%",
        squareRatio: 2.65,
        emphasis: {
            focus: 'self',
            blurScope: 'global' //coordinateSystem
        },
        tooltip: {
            // trigger: 'item',
            formatter: function(info) {
                let d = info.data;
                return `
                    <b>${d.code}</b><br/>
                    Open: ${d.open}<br/>
                    Close: ${d.close}<br/>
                `;
                 // ${d.logoLink ? `<img src="${d.logoLink}" style="width:42px;height:42px;">` : ''}
            }
        }
};
//====end option

//===================================Main================================

// Render ECharts
// https://apache.github.io/echarts-handbook/en/concepts/char-size/
// define chart name as "main-heatmap", with giving height and width 
var chartDom = document.getElementById('main-heatmap', null, {renderer: 'canvas', useDirtyRect: false});

// !!! chart intilization
var myChart = echarts.init(chartDom, 'dark');

// change the size chart when windows size change, Responsive
window.addEventListener('resize', function () {
    myChart.resize();
});


// !!! main information on chart rendering
if (option && typeof option === 'object') {
  myChart.setOption(option);
}

// event https://apache.github.io/echarts-handbook/en/concepts/event
myChart.on('dblclick', function(params) {
    // const code = params.name.split(" ")[0];
    window.open('http://localhost:8092/us/historystick/' + encodeURIComponent(params.name), "code", "location=0,width=600,height=400").focus();
    // window.open("url","winName","location=0,width=300,height=214").focus();

});

//========================Refresth =====================
// Function to update the chart with new data
function updateChart(data) {
    // Update treemapData with the new stocks data
    treemapData = data.map(stock => ({
        name: stock.code,
        value: Math.abs(stock.percentageChange),
        upDown: stock.percentageChange > 0 ? '▲' : stock.percentageChange < 0 ? '▼' : '-',
        currentPrice: stock.currentPrice,
        high: stock.high,
        low: stock.low,
        code: stock.code,
        logoLink: stock.logoLink,
        open: stock.open,
        close: stock.previousClose,
        itemStyle: {
            color: getHeatmapColor(stock.percentageChange)
        },
    }));

    // Update the chart with new data
    myChart.setOption({
        series: [{
            data: treemapData // Update series data
        }]
    });
}

// Refresh each 30s
function refresh() {
  $.ajax({
    url: "/us/realtime", // Your endpoint to fetch the updated coin data
    type: "GET",
    success: function(data) {
        stocks = data;
        updateChart(stocks);
        console.log("updated value : " + stocks.low)
    },
    // error: function(xhr, status, error) {
    //     console.error('Error fetching stock data:', error);
        // updateStatus('error', 'Connection Error');

        // Try again in 10 seconds if there's an error
        // setTimeout(refresh, 10000);
          
    //   }
  });
}

// jQuery
$(document).ready(function () {
  refresh();
  setInterval(refresh, 60000); // Update every 20 seconds
});