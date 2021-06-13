import {customElement, html, LitElement} from 'lit-element';
// import '@vaadin/vaadin-grid/src/vaadin-grid.js';
// import '@vaadin/vaadin-dialog/src/vaadin-dialog.js';
// import '../../components/search-bar.js';
// import '../../components/utils-mixin.js';
// import './order-card.js';
// import '../../../styles/shared-styles.js';

@customElement('file-to-sendfront-view')
export class FileToSendFrontView extends LitElement {

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
                    height: 100%;
                }
            </style>

            <search-bar id="search" show-checkbox=""></search-bar>

            <vaadin-grid id="grid" theme="orders no-row-borders"></vaadin-grid>

            <vaadin-dialog id="dialog" theme="orders" ></vaadin-dialog>
        `;
    }
    // on-opened-changed="_onDialogOpen"
    //
    // // Workaround for styling the dialog content https://github.com/vaadin/vaadin-dialog-flow/issues/69
    // _onDialogOpen(e) {
    //   if (!e.detail.value) {
    //     return;
    //   }
    //   var content = this.$.dialog.$.overlay.content;
    //   content.querySelector('flow-component-renderer').setAttribute('theme', 'dialog');
    // }
}

//
// window.customElements.define(FileToSendFrontView.is, FileToSendFrontView);
