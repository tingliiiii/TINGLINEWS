// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 從後端抓資料給 DataTable
const fetchData = async (uri) => {
    try {
        const response = await fetch(`http://${ip}:8080/tinglinews${uri}`); // 等待 fetch 請求完成
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
    // 只有編輯以上可以查看使用者管理頁面
    if (authorityId < 2) {
        $('.post-btn').css('display', 'none');
    }

    const userName = data.userName;
    const authorityName = data.authority.authorityName;
    $('#user').html(
        `<p>${authorityName}&ensp;${userName}&ensp;已登入</p>`
    );

    // 初始化 DataTable
    $('#news-table').DataTable({
        // 下載功能
        // dom: 'Bfrtip',
        //     buttons: [
        //         'copy', 'csv', 'excel', 'pdf', 'print'
        //     ],
        // 定義從後端獲取數據的 AJAX 請求
        ajax: async (data, callback, settings) => {
            // 從伺服器獲取數據
            const result = await fetchData('/admin/news');
            // 將數據傳遞給 DataTables 以製作表格
            if (Array.isArray(result)) {
                callback({ data: result });
            } else {
                console.error('Invalid data format:', result);
                callback({ data: [] });
            }
        },
        columns: [
            {
                data: 'newsId',
                render: (data) => `<button class="btn btn-success btn-sm update-news-btn" data-id=${data}>編輯</button>`
            },
            { data: 'newsId' },
            { data: 'title' },
            { data: 'userName' },
            { data: 'createdTime' },
            { data: 'updatedTime' },
            {
                data: 'public',
                render: (data, type, row) => `<span class="public-status" data-id=${row.newsId}>${data ? "是" : "否"}</span>`
            },

            { data: 'publicTime' }
        ],
        order: [[1, 'desc']],
        scrollX: true,
        scrollY: true
    }
    );

    // 點擊編輯按鈕
    $('#news-table').on('click', '.update-news-btn', async (event) => {

        // // console.log(event);
        const id = $(event.target).data('id');
        // console.log('按下編輯按鈕' + id);

        // 檢查使用者權限：只有編輯以上可以修改文章
        const authorityId = JSON.parse(sessionStorage.getItem('userData')).authority.authorityId;
        if (authorityId < 2) {
            Swal.fire('權限不足', '帳號權限有誤，請聯絡管理員', 'error');
            return;
        }

        const data = await fetchData(`/admin/news/${id}`);
        // // console.log(data);
        sessionStorage.setItem('newsData', JSON.stringify(data));
        window.location.replace('/tinglinews/emp/post.html');
    });

    // 點兩下變更發布狀態
    $('#news-table').on('dblclick', '.public-status', async (event) => {

        // 檢查使用者權限：只有編輯以上變更發布狀態
        const authorityId = JSON.parse(sessionStorage.getItem('userData')).authority.authorityId;
        // console.log(authorityId);
        if (authorityId < 2) {
            Swal.fire('權限不足', '帳號權限有誤，請聯絡管理員', 'error');
            return;
        }

        // 取得按鈕上的字
        const currentText = $(event.target).text();
        // 取得按鈕的 newsId
        const id = $(event.target).data('id');
        // console.log('變更發布狀態' + id);

        // 建立選擇器（用來切換狀態）
        const $span = $(event.target);
        const $select = $(`
            <select class="form-control">
                <option value="true" ${currentText === "是" ? "selected" : ""}>是</option>
                <option value="false" ${currentText === "否" ? "selected" : ""}>否</option>
            </select>
        `);

        // 若 select 狀態改變
        $select.on('change', async (event) => {

            if (currentText === "否") {
                const result = await Swal.fire({
                    title: '確定要公開此報導嗎？',
                    text: '',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: '確定',
                    cancelButtonText: '取消'
                })

                if (!result.isConfirmed) {
                    Swal.fire('報導未公開', '', 'info');
                    return;
                }
            } else if (currentText === "是") {
                const result = await Swal.fire({
                    title: '將報導改為不公開？',
                    text: '',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: '確定',
                    cancelButtonText: '取消'
                })
                if (!result.isConfirmed) {
                    Swal.fire('報導仍公開', '', 'info');
                    return;
                }
            }

            // 獲取新的狀態並轉換為布林值
            const newStatus = event.target.value === "true";

            // 發送更新請求到後端
            try {
                const response = await fetch(`http://${ip}:8080/tinglinews/admin/news/${id}/publish`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ public: newStatus }),
                });
                const { state, message, data } = await response.json();
                // console.log(state, message, data);
                if (state) {
                    Swal.fire(message, '', 'success');
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);

                } else {
                    console.error('更新失敗：', result.message);
                }
            } catch (error) {
                console.error('更新錯誤：', error);
            } finally {
                // 無論更新是否成功，都恢復原狀
                $span.show();
                $select.remove();
            }
        });
        // 隱藏 span 並插入 select
        $span.hide().after($select);



    });

    // 登出
    $('.logout-btn').on('click', () => {
        sessionStorage.clear();
        Swal.fire('登出成功', '', 'success');
        setTimeout(() => {
            window.location.replace('/tinglinews/emp/login.html');
        }, 1000);
    });

    // 點兩下預覽新聞
    /*
    $('#news-table').on('dblclick', '.sorting_1', async (event) => {

        const id = $(event.target).closest('tr').find('.update-news-btn').data('id');
        console.log('預覽新聞 ID：' + id);
        const data = await fetchData(`/emp/news/${id}`);
        console.log(data);
        
        $('#title').text(data.title);
        $('#content').html(data.content);
        $('#newsModal').modal('show');


        
        Swal.fire({
            title: '預覽新聞',
            html: `
                <h3 style="font-size: 1.5em;">${data.title}</h3>
                <p style="font-size: .8em;">${data.content}</p>
            `,
            showCloseButton: true,
            showConfirmButton: false
        });
        
    })
*/

});
