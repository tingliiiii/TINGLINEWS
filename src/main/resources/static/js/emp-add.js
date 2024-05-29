$(document).ready(() => {

  tinymce.init({
    selector: '#articleContent',
    language: 'zh_TW',
    plugins: 'autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount checklist mediaembed casechange formatpainter pageembed linkchecker a11ychecker powerpaste autocorrect inlinecss markdown',
    toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | a11ycheck | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat'
  });

})
