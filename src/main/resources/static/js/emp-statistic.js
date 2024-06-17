const ip = '172.20.10.5';

const fetchData = async (uri) => {
    try {
        const response = await fetch(`http://${ip}:8080/tinglinews/admin/statistic${uri}`); // 等待 fetch 請求完成
        const { state, message, data } = await response.json(); // 等待回應本文內容
        // console.log(state, message, data);
        if (state) {
            return data;
        } else {
            console.error('Error fetching data:', message);
            return [];
        }
    } catch (e) {
        console.error('Fetch error:', e);
        return [];
    }
};

async function drawNewsChart() {

    const result = await fetchData('/topsavednews');

    // 檢查是否有數據
    if (result.length === 0) {
        console.error('No data returned from fetchData.');
        return;
    }

    const chartData = [
        ['新聞', 'count', { role: 'annotation' }]
    ];

    result.forEach(news => {
        chartData.push([String(news.newsId), news.count, news.title]);
    });

    var data = google.visualization.arrayToDataTable(chartData);


    var options = {
        title: '新聞收藏數排行榜', // 圖表標題
        width: 800, // 圖表寬度
        height: 400, // 圖表高度
        backgroundColor: '#eee', // 設置整個圖表的背景顏色，包括外部標題和圖示
        legend: { position: 'top', alignment: 'center' }, // 圖例的位置和對齊方式
        bars: 'horizontal', // 條形圖的方向，可以是 'vertical' 或 'horizontal'
        colors: ['#75B8C8'], // 自訂條形顏色
        hAxis: {
            title: '被收藏次數', // 水平軸標題
            minValue: 0, // 水平軸最小值
            format: '#' // 只顯示整數
        },
        vAxis: {
            title: '新聞' // 垂直軸標題
        },
        fontName: 'Noto Sans TC', // 字體名稱
        fontSize: 12, // 字體大小
        titleTextStyle: {
            color: '#333', // 標題文字顏色
            fontSize: 18, // 標題文字大小
            bold: true, // 粗體
            italic: false, // 斜體
            padding: 10, // 標題內邊距
        },
        chartArea: {
            width: '50%',
            height: '70%'
        } // 圖表區域的寬度和高度
    };
    // PieChart, BarChart, ColumnChart, LineChart
    var chart = new google.visualization.BarChart(document.getElementById('newschart'));
    chart.draw(data, options);
}

async function drawJounalistChart() {

    const result = await fetchData('/topjournalists');

    // 檢查是否有數據
    if (result.length === 0) {
        console.error('No data returned from fetchData.');
        return;
    }

    const chartData = [
        ['記者', 'count']
    ];

    result.forEach(data => {
        chartData.push([data.journalistName, data.count]);
    });

    var data = google.visualization.arrayToDataTable(chartData);


    var options = {
        title: '記者收藏數冠軍榜', // 圖表標題
        width: 800, // 圖表寬度
        height: 400, // 圖表高度
        backgroundColor: '#eee', // 設置整個圖表的背景顏色，包括外部標題和圖示
        legend: { position: 'top', alignment: 'center' }, // 圖例的位置和對齊方式
        bars: 'horizontal', // 條形圖的方向，可以是 'vertical' 或 'horizontal'
        colors: ['#D4DF9E'], // 自訂條形顏色
        hAxis: {
            title: '被收藏次數', // 水平軸標題
            minValue: 0, // 水平軸最小值
            format: '#' // 只顯示整數
        },
        vAxis: {
            title: '記者' // 垂直軸標題
        },
        fontName: 'Noto Sans TC', // 字體名稱
        fontSize: 12, // 字體大小
        titleTextStyle: {
            color: '#333', // 標題文字顏色
            fontSize: 18, // 標題文字大小
            bold: true, // 粗體
            italic: false, // 斜體
            padding: 10, // 標題內邊距
        },
        chartArea: {
            width: '50%',
            height: '70%'
        } // 圖表區域的寬度和高度
    };
    // PieChart, BarChart, ColumnChart, LineChart
    var chart = new google.visualization.BarChart(document.getElementById('journalistchart'));
    chart.draw(data, options);
}
$(document).ready(async () => {

    const data = JSON.parse(sessionStorage.getItem('userData'));
    if (!data) {
        window.location.replace('/tinglinews/emp/login.html');
        return;
    }
    const authorityId = data.authority.authorityId;
    if (authorityId < 1) {
        window.location.replace('/tinglinews/emp/login.html');
        return;
    }
    const userName = data.userName;
    const authorityName = data.authority.authorityName;
    $('#user').html(
        `<p>${authorityName}&ensp;${userName}&ensp;已登入</p>`
    );

    google.charts.load('current', { 'packages': ['corechart'] });
    google.charts.setOnLoadCallback(drawChart);

    function drawChart() {
        drawNewsChart();
        drawJounalistChart();
    }

    // 登出
    $('.logout-btn').on('click', () => {
        sessionStorage.clear();
        Swal.fire('登出成功', '', 'success');
        setTimeout(() => {
            window.location.replace('/tinglinews/emp/login.html');
        }, 1000);
    });
})