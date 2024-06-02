// 從後端抓資料給 DataTable
const fetchData = async (uri) => {
    const url = `http://localhost:8080/tinglinews${uri}`;
    try {
        const response = await fetch(url); // 等待 fetch 請求完成
        const { state, message, data } = await response.json(); // 等待回應本文內容
        console.log(state, message, data);
        return data;
    } catch (e) {
        console.error(e);
        return [];
    }
};

$(document).ready(() => {

    $('#news-table').DataTable({
        // 下載功能
        // dom: 'Bfrtip',
        //     buttons: [
        //         'copy', 'csv', 'excel', 'pdf', 'print'
        //     ],
        ajax: async (data, callback, settings) => {
            // 從伺服器獲取數據
            const result = await fetchData('/emp/news');
            // 將數據傳遞給 DataTables 以製作表格
            callback({
                data: result
            });
        },
        columns: [
            {
                data: 'newsId',
                render: (data) => `<button class="btn btn-success btn-sm update-news-btn" data-id=${data}>修改</button>`
            },
            { data: 'newsId' },
            { data: 'title' },
            { data: 'userName' },
            { data: 'createdTime' },
            { data: 'updatedTime' },
            {
                data: 'public',
                render: (data) => data ? "是" : "否"
            },
            { data: 'publicTime' }
        ],
        order: [[1, 'desc']],
        scrollX: true,
        scrollY: true
    }
    );

    $('#news-table').on('click', '.update-news-btn', async (event) => {

        // console.log(event);
        const id = $(event.target).data('id');
        console.log('按下修改按鈕' + id);

        const data = await fetchData(`/emp/news/${id}`);
        // console.log(data);
        sessionStorage.setItem('data', JSON.stringify(data));
        window.location.href = '/tinglinews/emp/post.html';
    });


});
