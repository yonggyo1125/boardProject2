window.addEventListener("DOMContentLoaded", function() {
    const formActions = document.getElementsByClassName("form-action");
    for (const el of formActions) {
        el.addEventListener("click", function() {
            const mode = this.dataset.mode;
            const formName = this.dataset.formName;

            const frm = document.forms[formName];
            if (!frm._method) return;

            frm._method.value = mode == 'delete' ? 'DELETE' : 'PATCH';
            const confirmMsg = `정말 ${mode == 'delete' ? '삭제':'수정'} 하시겠습니까?`;
            if (confirm(confirmMsg)) {
                frm.submit();
            }
        });
    }
});