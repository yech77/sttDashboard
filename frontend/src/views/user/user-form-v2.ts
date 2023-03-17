import {customElement, html, LitElement} from 'lit-element';
import {FormLayoutResponsiveStep} from "@vaadin/vaadin-form-layout";
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-checkbox';
import '@vaadin/vaadin-radio-button/vaadin-radio-button';
import '@vaadin/vaadin-radio-button/vaadin-radio-group';
import '../../components/buttons-bar.js'


// @ts-ignore
@customElement('user-form-v2')
export class UserFormV2 extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    private responsiveSteps: FormLayoutResponsiveStep[] = [
        {columns: 1, labelsPosition: 'top'},
        {minWidth: '600px', columns: 2, labelsPosition: 'top'}
    ];

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
            <vaadin-form-layout id="form1" .responsiveSteps="${this.responsiveSteps}">
                <vaadin-text-field id="userName" label="Nombre" clear-button-visible></vaadin-text-field>
                <vaadin-text-field id="userLastname" label="Apellido" clear-button-visible></vaadin-text-field>
                <vaadin-vertical-layout>
                    <vaadin-email-field id="userEmail" label="Email" clear-button-visible
                                        style="width: 100%"></vaadin-email-field>
                    <vaadin-checkbox id="active" checked>activo</vaadin-checkbox>
                </vaadin-vertical-layout>
                <vaadin-password-field id="password" label="Password"></vaadin-password-field>
                <vaadin-combo-box id="clients" label="Cliente" clear-button-visible></vaadin-combo-box>
                <br>
                <vcf-multiselect-combo-box id="systemids" label="Credenciales"
                                           clear-button-visible></vcf-multiselect-combo-box>
            </vaadin-form-layout>

            <buttons-bar id="footer" no-scroll\\$="[[noScroll]]">
                <vaadin-button slot="left" id="cancel">Cancelar</vaadin-button>
                <vaadin-button slot="right" id="save" theme="primary">Crear usuario
                    <iron-icon icon="vaadin:arrow-right" slot="suffix"></iron-icon>
                </vaadin-button>
            </buttons-bar>

        `;
    }
}