// 從後端抓資料
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

// 新增文章的標籤選項
const loadTags = async () => {
  try {
    const data = await fetchData('/emp/tags');
    const select = $('#tags');

    data.forEach(tag => {
      const option = $('<option></option>');
      option.attr('value', tag.tagId);
      option.text(tag.tagName);
      select.append(option);
    });
    /*
    $.each(data, (index, tag) => {
      const option = $('<option></option>');
      option.attr('value', tag.tagId);
      option.text(tag.tagName);
      select.append(option);
    });
    */
  } catch (error) {
    console.error('Error fetching data:', error);
  }

};

// 表單提交事件處理
const handleSubmit = async (event) => {

  event.preventDefault();

  const formData = {
    title: $('#title').val(),
    tagId: $('#tags').val(),
    content: tinymce.get('content').getContent(),
    userId: 1032
    // sessionStorage.getItem('userId')
  };

  if ($('#submit-btn').text() === '新增文章') {
    await submitPost(formData);
  } else {
    await updatePost(formData);
  }

};

// 新增文章
const submitPost = async (formData) => {
  try {

    const response = await fetch('http://localhost:8080/tinglinews/emp/post', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formData),
      // credentials: 'include' // 確保请求包含 cookies
    });

    const { state, message, data } = await response.json();
    console.log(state, message, data);

    if (state) {
      Swal.fire('新增成功', message, 'success');
      setTimeout(() => {
        window.location.replace('/tinglinews/emp/content-management.html');
      }, 1000);
    } else {
      Swal.fire('新增文章失敗', message, 'warning');
    }
  } catch (error) {
    console.error('新增文章錯誤：', error);
    Swal.fire('新增文章錯誤 請稍後再試', error, 'error');
  }

};

// 修改文章
const updatePost = async (formData) => {
  try { 
    const newsId = JSON.parse(sessionStorage.getItem('data')).newsId; 
    const response = await fetch(`http://localhost:8080/tinglinews/emp/news/${newsId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formData),
      // credentials: 'include' // 確保請求包含 cookies
    });

    const { state, message, data } = await response.json();
    console.log(state, message, data);

    if (state) {
      Swal.fire('修改成功', message, 'success');
      setTimeout(() => {
        window.location.replace('/tinglinews/emp/content-management.html');
      }, 1000);
    } else {
      Swal.fire('修改文章失敗', message, 'warning');
    }
  } catch (error) {
    console.error('修改文章錯誤：', error);
    Swal.fire('修改文章錯誤 請稍後再試', error, 'error');
  }

};

$(document).ready(() => {

  loadTags();

  // 內文編輯器
  tinymce.init({
    selector: '#content',
    language: 'zh_TW',
    plugins: 'autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount checklist mediaembed casechange formatpainter pageembed linkchecker a11ychecker powerpaste autocorrect inlinecss markdown',
    toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | a11ycheck | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat'
  });

  // 如果 sessionStorage 中有 data 就是修改文章
  const data = JSON.parse(sessionStorage.getItem('data'));
  if (data != null) {
    // console.log(sessionStorage);
    // console.log(data.newsId);
    $('#title').val(data.title);
    $('#tags').val(data.tagId);
    tinymce.get('content').on('init', (event) => {
      event.target.setContent(data.content);
    });
    $('#submit-btn').text('修改');
  }
  $('#post-form').on('submit', handleSubmit);

})
