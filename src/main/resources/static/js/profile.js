// 監聽事件處理
const handleEvent = async (event, className, callback) => {
    console.log($(event.target).attr('class'));

    if (!$(event.target).hasClass(className)) {
        return;
    }
    const id = $(event.target).data('id');
    callback(id); // 返回值
};

// 刪除使用者
const handleDeleteUser = async (id) => {
    console.log('按下刪除' + id);

    // 使用 sweetalert2 顯示確認刪除訊息框
    const result = await Swal.fire({
        title: '確定要刪除嗎？',
        text: '刪除後將無法回復',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: "Yes, delete it!",
        cancelButtonText: "No, cancel!"
    });
    if (!result.isConfirmed) {
        Swal.fire("並未刪除", "", "info"); // title, text, icon
        return;
    } else {
        Swal.fire("刪除成功", "", "success");
    }
    // 刪除程序
    /*
    try {
        const fullUrl = `http://localhost:8080/SpringMVC/mvc/rest/user/${id}`;
        const response = await fetch(fullUrl, { method: 'DELETE' }); // 等待 fetch 請求完成
        const { state, message, data } = await response.json(); // 等待回應本文內容
        // console.log(state, message, data);
        Swal.fire("刪除成功", "", "success");
        // 更新 user list
        fetchAndRenderData('/mvc/rest/user', 'user-list-body', renderUser);
    } catch (e) {
        console.error("刪除過程中發生錯誤：", e);
        Swal.fire("刪除失敗", e.message, "error");
    }
    */

};
// 資料渲染 ================================================================

// 渲染 User 資料配置
const renderDonated = ({ id, name, gender, age, birth, education, interestNames, resume }) => `
    <tr>
   
    <td>${id}</td><td>${name}</td><td>${age}</td>
        <td>${birth}</td><td>${education.name}</td><td>${interestNames}</td>
        
        <td>
            <span class="btn btn-danger stop-donated-btn" data-id="${id}">停止</span>
        </td>
           
    </tr>`;

const renderSaved = ({ number, name, price, description }) => `
<tr>
<td>${number}</td><td>${description}</td><td>${name}</td><td>${price}</td>
<td class="text-center">
    <span class="delete-saved-btn" data-id="${number}">✕</td>
</tr>
`;

// 資料渲染（資料所在地，目標位置，渲染方法）
const fetchAndRenderData = async (endpointUri, containerId, renderFn) => {
    const url = 'http://localhost:8080/SpringMVC' + endpointUri;
    $.getJSON(url, (response) => {
        const { state, message, data } = response;
        $('#' + containerId).html(Array.isArray(data) ? data.map(renderFn).join('') : renderFn(data));
    }).fail((e) => {
        console.error(e);
        $('#' + containerId).html('無法加載資料');
    });
};

const fetchAndRenderData2 = async (url, containerId, renderFn) => {
    $.getJSON(url, (data) => {
        $('#' + containerId).html(Array.isArray(data) ? data.map(renderFn).join('') : renderFn(data));
    }).fail((e) => {
        console.error(e);
        $('#' + containerId).html('無法加載資料');
    });
};

$(document).ready(() => {

    $('.header-container').load('../nav-login.html');
    $('.footer-container').load('../footer.html');
    $('.ad-container').load('../ad.html');

    fetchAndRenderData('/mvc/rest/user', 'donated-list-body', renderDonated);
    fetchAndRenderData2('https://cwpeng.github.io/live-records-samples/data/products.json', 'saved-list-body', renderSaved);

    $('#toggledDnatedList').on('click', () => {
        $('#saved-list').hide();
        $('#donated-list').toggle();
    });

    $('#toggleSavedList').on('click', () => {
        $('#donated-list').hide();
        $('#saved-list').toggle();
    });

    // 監聽 User List 點擊事件
    $('#donated-table').on("click", (event) => {
        handleEvent(event, 'stop-donated-btn', handleDeleteUser);
    });
    $('#saved-table').on("click", (event) => {
        handleEvent(event, 'delete-saved-btn', handleDeleteUser);
    });



    // $('#donated-table').DataTable();

});

