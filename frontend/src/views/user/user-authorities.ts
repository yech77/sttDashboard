import {customElement, html, LitElement} from 'lit-element';
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-checkbox';
import '@vaadin/vaadin-radio-button/vaadin-radio-button';
import '@vaadin/vaadin-radio-button/vaadin-radio-group';
import '../../components/buttons-bar.js'


// @ts-ignore
@customElement('user-authorities')
export class UserAuthorities extends LitElement {
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
            <vaadin-combo-box id="clients" label="Usuarios" clear-button-visible></vaadin-combo-box>
            <vaadin-board>
                <vaadin-row>
                    <vaadin-horizontal-layout style="width: 100%">
                        <vaadin-checkbox-group id="checkbox1" theme="vertical"
                                               style="width: 50%"></vaadin-checkbox-group>
                        <vaadin-checkbox-group id="checkbox2" theme="vertical"
                                               style="width: 50%"></vaadin-checkbox-group>
                    </vaadin-horizontal-layout>
                </vaadin-row>
            </vaadin-board>

            <buttons-bar id="footer" no-scroll\\$="[[noScroll]]">
                <vaadin-button slot="left" id="cancel">Cancelar</vaadin-button>
                <vaadin-button slot="right" id="save" theme="primary">
                    Guardar
                    <iron-icon icon="vaadin:arrow-right" slot="suffix"></iron-icon>
                </vaadin-button>
            </buttons-bar>

        `;
    }
}