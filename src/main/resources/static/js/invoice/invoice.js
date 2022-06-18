const Controller = {

    products: {},
    stocks: {},


    init: function () {
        Controller.loadProducts(function () {
            Controller.getInvoices();
            Controller.loadStocks();
        });
    },

    getInvoices: function () {
        let minDate = $('#invoiceSearchMinDate').val();
        if (minDate === '')
            minDate = undefined;
        let maxDate = $('#invoiceSearchMaxDate').val();
        if (maxDate === '')
            maxDate = undefined;
        const person = $('#invoiceSearchPerson').val();
        const personNameIgnoreCase = $('#personSearchNameIgnoreCase').is(':checked');
        const personNameStartsWith = $('#personNameSearchStartsWith').is(':checked');
        const minCount = $('#invoiceSearchMinCount').val();
        const maxCount = $('#invoiceSearchMaxCount').val();
        const productId = $('#invoiceProductSearchSelect').val();
        const invoiceType = $('#invoiceTypeSearch').val();
        const invoiceDirection = $('#invoiceDirectionSearch').val();
        let sortType = $('#invoiceSortSelect').val();
        if (typeof sortType != 'string')
            sortType = 'none';
        const sortInverse = $('#inverseSort').is(':checked');
        $.ajax({
            type: 'POST',
            url: `/api/invoice/all?sort=${sortType}&inverse=${sortInverse}&${minDate === undefined ? '' : `min_date=${new Date(minDate).getTime()}&`}${maxDate === undefined ? '' : `max_date=${new Date(maxDate).getTime()}`}${person === '' ? '' : `person=${person}&`}person_name_ignore_case=${personNameIgnoreCase}&person_name_starts_with=${personNameStartsWith}&${minCount === '' ? '' : `min_count=${minCount}&`}${maxCount === '' ? '' : `max_count=${maxCount}&`}${productId === 'none' ? '' : `product_id=${productId}&`}${invoiceType === 'none' ? '' : `type=${invoiceType}&`}${invoiceDirection === 'none' ? '' : `invoice_direction=${invoiceDirection}`}`,
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                Controller.fillTable(data);
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    fillTable: function (data) {
        const tbody = $('#invoicesTableBody')[0];
        let rows = ``;
        for (let i = 0; i < data.length; i++) {
            rows +=
                `
                    <tr>
                        <td><label><input class="form-check-input select-table-item" type="checkbox" value="${data[i]['id']}"></label></td>
                        <td>${data[i]['id']}</td>
                        <td><a href="javascript:void(0)" onclick="Controller.showProductModal(${data[i]['product']})">${Controller.getProduct(data[i]['product'])['name']}</a></td>
                        <td>${data[i]['count']}</td>
                        <td>${data[i]['invoice_type']['localed']}</td>
                        <td>${data[i]['invoice_direction']['localed']}</td>
                        <td>${data[i]['person']}</td>
                        <td>${data[i]['invoice_date']}</td>
                    </tr>
                `
        }
        tbody.innerHTML = rows;
    },

    getProduct: function (id) {
        return Controller.products[id.toString()];
    },

    getStock: function (id) {
        return Controller.stocks[id.toString()];
    },

    loadProducts: function (callback) {
        const selection = $('#productIdSelection')[0];
        let productSearchSelect = $('#invoiceProductSearchSelect')[0];
        $.ajax({
            type: 'POST',
            url: '/api/product/all',
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                let productsOption = `<option disabled selected value="none">Выберите товар</option>`;
                productSearchSelect.innerHTML = `<option selected value="none">Продукт</option>`;
                for (let i = 0; i < data.length; i++) {
                    Controller.products[data[i]['id'].toString()] = data[i];
                    productsOption += `<option value="${data[i]['id']}">${data[i]['id']} | ${data[i]['name']}</option>`;
                    productSearchSelect.innerHTML += `<option value="${data[i]['id']}">${data[i]['id']} | ${data[i]['name']}</option>`;
                }
                selection.innerHTML = productsOption;
                callback();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    loadStocks: function () {
        $.ajax({
            type: 'POST',
            url: '/api/stock/all',
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                for (let i = 0; i < data.length; i++) {
                    Controller.stocks[data[i]['stock_id']] = data[i];
                }
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    createInvoice: function () {
        let productId;
        if (isNaN(productId = Number.parseInt($('#productIdSelection').val()))) return;
        let count;
        if (isNaN(count = Number.parseInt($('#productCount').val()))) return;
        const person = $('#invoicePerson').val();
        if (person === '') return;
        const invoiceType = $('#invoiceTypeSelect').val();
        if (invoiceType === '') return;
        const date = new Date($('#invoiceDate').val());
        const invoice_date = parseToValidDate(date);
        const invoice_direction = $('#invoiceDirectionSelect').val();
        const invoice = {
            'product': productId,
            'invoice_type': invoiceType,
            'invoice_direction': invoice_direction,
            'count': count,
            'person': person,
            'invoice_date': invoice_date
        };
        $.ajax({
            type: 'POST',
            url: '/api/invoice/add',
            contentType: 'application/json',
            data: JSON.stringify(invoice),
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                $('#productIdSelection option').prop('selected', function() {
                    return this.defaultSelected;
                });
                $('#productCount').val('');
                $('#invoicePerson').val('');
                $('#invoiceTypeSelect option').prop('selected', function() {
                    return this.defaultSelected;
                });
                $('#invoiceDirectionSelect option').prop('selected', function() {
                    return this.defaultSelected;
                });
                $('#invoiceDate').val('');
                const remainderElement = $('#productRemainder', true);
                remainderElement.attr('hidden');
                Controller.init();
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    showProductModal: function (id) {
        const modal = new bootstrap.Modal($('#productModal')[0]);
        const product = Controller.getProduct(id);
        const stock = Controller.getStock(product['stock_id']);
        $('#modalProductId').text(product['id']);
        $('#modalProductName').text(product['name']);
        $('#modalProductCount').text(product['count']);
        $('#modalProductPrice').text(product['price'] + " руб./шт");
        $('#modalProductDate').text(product['purchase_date']);
        $('#modalProductStock').text(stock['stock_name'] + " | " + stock['stock_address']);
        modal.show();
    },

    selectAll: function () {
        $('.select-table-item').each(function (i, item) {
            item.checked = $('#selectBoxAll').is(":checked");
        })
    },

    deleteSelectedInvoices: function () {
        const invoiceIds = [];
        let index = 0;
        $('.select-table-item').each(function (i, item) {
            if (item.checked) {
                invoiceIds[index] = item.value;
                index++;
            }
        });
        $('#selectBoxAll').prop('checked', false);
        $.ajax({
            type: 'POST',
            url: '/api/invoice/delete',
            data: JSON.stringify(invoiceIds),
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                Controller.getInvoices();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    deleteInvoice: function () {
        const id = $('#deleteInvoiceId').val();
        $.ajax({
            type: 'POST',
            url: `/api/invoice/delete?id=${id}`,
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                Controller.getInvoices();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    resetSearchFields: function () {
        $('#invoiceSearchMinDate').val();
        $('#invoiceSearchMaxDate').val();
        $('#invoiceSearchPerson').val();
        $('#personSearchNameIgnoreCase').is(':checked');
        $('#personNameSearchStartsWith').is(':checked');
        $('#invoiceSearchMinCount').val();
        $('#invoiceSearchMaxCount').val();
        $('#invoiceProductSearchSelect option').prop('selected', function() {
            return this.defaultSelected;
        });
        $('#invoiceTypeSearch option').prop('selected', function() {
            return this.defaultSelected;
        });
        $('#invoiceDirectionSearch option').prop('selected', function() {
            return this.defaultSelected;
        });
    }
}