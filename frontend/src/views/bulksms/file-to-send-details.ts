import {customElement, html, LitElement} from 'lit-element';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-form-layout/src/vaadin-form-item.js';
import '@vaadin/vaadin-form-layout/src/vaadin-form-layout.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '../../components/buttons-bar.js';
import '../../components/utils-mixin.js';
import '../storefront/order-status-badge.js';
import '../../../styles/shared-styles.js';
// import {html} from '@polymer/polymer/lib/utils/html-tag.js';

// class OrderDetails extends window.ScrollShadowMixin(PolymerElement) {

@customElement('file-to-send-details')
export class FileToSendDetails extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <style include="shared-styles">
                :host {
                    display: flex;
                    flex-direction: column;
                    box-sizing: border-box;
                    flex: auto;
                }

                /*
                  Workaround for non-working dom-repeat inside tables in IE11
                  (https://github.com/Polymer/polymer/issues/1567):
                  use divs with table-like display values instead of the actual
                  <table>, <tr> and <td> elements.
                */
                .table {
                    display: table;
                }

                .tr {
                    display: table-row;
                }

                .td {
                    display: table-cell;
                }

                .main-row {
                    flex: 1;
                }

                h3 {
                    margin: 0;
                    word-break: break-all;
                    /* Non standard for WebKit */
                    word-break: break-word;
                    white-space: normal;
                }

                .date,
                .time {
                    white-space: nowrap;
                }

                .dim,
                .secondary {
                    color: var(--lumo-secondary-text-color);
                }

                .secondary {
                    font-size: var(--lumo-font-size-xs);
                    line-height: var(--lumo-font-size-xl);
                }

                .meta-row {
                    display: flex;
                    justify-content: space-between;
                    padding-bottom: var(--lumo-space-s);
                }

                .products {
                    width: 100%;
                }

                .products .td {
                    text-align: center;
                    vertical-align: middle;
                    padding: var(--lumo-space-xs);
                    border: none;
                    border-bottom: 1px solid var(--lumo-contrast-10pct);
                }

                .products .td.product-name {
                    text-align: left;
                    padding-left: 0;
                    width: 100%;
                }

                .products .td.number {
                    text-align: right;
                }

                .products .td.money {
                    text-align: right;
                    padding-right: 0;
                }

                .history-line {
                    margin: var(--lumo-space-xs) 0;
                }

                .comment {
                    font-size: var(--lumo-font-size-s);
                }

                order-status-badge[small] {
                    margin-left: 0.5em;
                }

                #sendComment {
                    color: var(--lumo-primary-color-50pct);
                }

                @media (min-width: 600px) {
                    .main-row {
                        padding: var(--lumo-space-l);
                        flex-basis: auto;
                    }
                }
            </style>

            <div class="scrollable main-row" id="main"></div>

            <buttons-bar id="footer" no-scroll\$="[[noScroll]]">
                <vaadin-button slot="left" id="back" hidden="[[!review]]">Back</vaadin-button>
                <vaadin-button slot="left" id="cancel" hidden="[[review]]">Cancel</vaadin-button>

                <div slot="info" class="total">Total 0/div>

                <vaadin-button slot="right" id="save" theme="primary success" hidden="[[!review]]">
                    <iron-icon icon="vaadin:check" slot="suffix"></iron-icon>
                    Place Program
                </vaadin-button>
                <vaadin-button slot="right" id="edit" theme="primary" hidden="[[review]]">
                    Edit Program
                    <iron-icon icon="vaadin:edit" slot="suffix"></iron-icon>
                </vaadin-button>
            </buttons-bar>
        `;
    }
    //
    // static get is() {
    //     return 'order-details';
    // }

    // static get properties() {
    //     return {
    //         item: {
    //             type: Object
    //         }
    //     };
    // }

    // ready() {
    //     // super.ready();
    //
    //     this.$.form1.responsiveSteps = this.$.form3.responsiveSteps = [
    //         {columns: 1, labelsPosition: 'top'},
    //         {minWidth: '600px', columns: 4, labelsPosition: 'top'}
    //     ];
    //
    //     this.$.form2.responsiveSteps = [
    //         {columns: 1}, {minWidth: '180px', columns: 2}
    //     ];
    //
    //     this.$.form4.responsiveSteps = [
    //         {columns: 1, labelsPosition: 'top'}
    //     ];
    // }

    // _onCommentKeydown(event) {
    //     if (event.key === 'Enter' || event.keyCode == 13) {
    //         // In IE11 on button click commentField blur doesn't happen, and the value-change event is not fired
    //         this.$.commentField.blur();
    //         this.$.sendComment.click();
    //     }
    // }
}
//
// window.customElements.define(OrderDetails.is, OrderDetails);
