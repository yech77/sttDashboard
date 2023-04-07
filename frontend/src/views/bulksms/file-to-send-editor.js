import {html, LitElement} from 'lit-element';
import {FormLayoutResponsiveStep} from "@vaadin/vaadin-form-layout";

class FileToSendEditor extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    // private responsiveSteps: FormLayoutResponsiveStep[] = [
    //     {columns: 1, labelsPosition: 'top'},
    //     {minWidth: '600px', columns: 4, labelsPosition: 'top'}
    // ];
    // private form2: FormLayoutResponsiveStep[] = [
    //     {columns: 1, labelsPosition: 'top'},
    //     {minWidth: '360px', columns: 2, labelsPosition: 'top'}
    // ];
    // private form3: FormLayoutResponsiveStep[] = [
    //     {columns: 1, labelsPosition: 'top'},
    //     {minWidth: '500px', columns: 3, labelsPosition: 'top'}
    // ];

    render() {
        return html`

            <style include="shared-styles">
                :host {
                    display: flex;
                    flex-direction: column;
                    flex: auto;
                }

                .meta-row {
                    display: flex;
                    justify-content: space-between;
                    padding-bottom: var(--lumo-space-s);
                }

                .dim {
                    color: var(--lumo-secondary-text-color);
                    text-align: right;
                    white-space: nowrap;
                    line-height: 2.5em;
                }

                .status {
                    width: 10em;
                }
            </style>
            <div class="scrollable flex1" id="main">
                <h2 id="title">Nueva programacion</h2>
                <span style="font-size: var(--lumo-font-size-m); color: var(--lumo-secondary-text-color)">Recuerda, si utilizas acentos y la letra ñ, éstos serán cambiados por la vocal sin acento y letra n</span>

                <div class="meta-row" id="metaContainer">
                    <span class="dim">Order #<span id="orderNumber"></span></span>
                </div>

                <vaadin-form-layout id="form1" .responsiveSteps="${this.responsiveSteps}">
                    <vaadin-form-layout id="form2" .responsiveSteps="${this.form2}">
                        <vaadin-date-picker label="Fecha de envío" id="dueDate2"></vaadin-date-picker>
                        <vaadin-time-picker label="Hora de envío" id="dueTime"></vaadin-time-picker>
                        <vaadin-checkbox id="sendNow">Enviar ya</vaadin-checkbox>
                        <vaadin-date-time-picker label="Fecha de envío" id="dueDate"></vaadin-date-time-picker>
                    </vaadin-form-layout>

                    <vaadin-form-layout id="form3" colspan="3" .responsiveSteps="${this.form3}">
                        <vaadin-combo-box id="status" label="Agenda" class="status" colspan="2"
                                          clear-button-visible></vaadin-combo-box>
                        <!--                        <iron-icon slot="prefix" icon="vaadin:phone"></iron-icon>-->
                        </vaadin-combo-box>
                        <vaadin-combo-box id="systemId" label="Credencial"
                                          clear-button-visible></vaadin-combo-box>
                        <vaadin-text-field id="orderName" label="Nombre" colspan="2"
                                           clear-button-visible></vaadin-text-field>
                        <vaadin-text-field id="orderDescription" label="Descripcion" colspan="2"
                                           clear-button-visible></vaadin-text-field>
                        <vaadin-text-area id="message" label="Escriba su mensaje" colspan="2" required
                                          clear-button-visible></vaadin-text-area>
                        <vaadin-text-area id="messageBuilded" label="Vista previa"
                                          helper-text="Mensaje a enviar al usuario (Calculado para agendas con variables)"
                                          readonly="true"></vaadin-text-area>

                        <vaadin-form-item colspan="2">
                            <hr>
                            <vaadin-checkbox id="acceptCheckbox"></vaadin-checkbox>
                            <p id="charCounter"></p>
                            <span id="warningSpan"></span>
                        </vaadin-form-item>
                    </vaadin-form-layout>
                </vaadin-form-layout>
            </div>

            <buttons-bar id="footer" no-scroll\\$="[[noScroll]]">
                <vaadin-button slot="left" id="cancel">Cancelar</vaadin-button>
                <vaadin-button slot="right" id="review" theme="primary">
                    Confirmar programacion
                    <iron-icon icon="vaadin:arrow-right" slot="suffix"></iron-icon>
                </vaadin-button>
            </buttons-bar>

        `;
    }

    //
    // static get is() {
    //     return 'file-to-send-editor';
    // }
    //
    // static get properties() {
    //     return {
    //         status: {
    //             type: String,
    //             observer: '_onStatusChange'
    //         }
    //     };
    // }

    ready() {
        // super.ready();

        // Not using attributes since Designer does not suppor single-quote attributes
        this.form1.responsiveSteps = [];
        this.$.form2.responsiveSteps = [
            {columns: 1, labelsPosition: 'top'},
            {minWidth: '360px', columns: 2, labelsPosition: 'top'}
        ];
        this.$.form3.responsiveSteps = [
            {columns: 1, labelsPosition: 'top'},
            {minWidth: '500px', columns: 3, labelsPosition: 'top'}
        ];
    }
}

customElements.define('file-to-send-editor', FileToSendEditor)