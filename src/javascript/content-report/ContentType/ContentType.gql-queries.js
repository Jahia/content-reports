import gql from 'graphql-tag';

const ContentValuesQuery = gql`
    query nodeByType($nodeType: String!, $language: String!) {
        jcr(workspace: EDIT) {
            nodesByCriteria(criteria: { nodeType: $nodeType, language: $language }) {
                nodes {
                    displayName(language: $language)
                    primaryNodeType {
                        displayName(language: $language)
                    }
                    createdBy: property(name: "jcr:createdBy", language: $language) {
                        value
                    }
                    lastModified: aggregatedLastModifiedDate(language: $language)
                    properties(language: $language,
                        fieldFilter: {
                            filters: [
                                {
                                    evaluation: EQUAL
                                    fieldName: "definition.hidden"
                                    value: "false"
                                }
                            ]
                        }
                    ) {
                        name
                        value
                        definition {
                            displayName(language: $language)
                        }
                    }
                }
            }
        }
    }
`;

const ContentTypeListQuery = gql`
    query ContentTypeListQuery($language: String!, $query: String!) {
        jcr(workspace: EDIT) {
            nodesByQuery(query: $query, language: $language) {
                nodes {
                    displayName(language: $language)
                    primaryNodeType {
                        name
                        displayName(language: $language)
                    }
                }
            }
        }
    }
`;
export {ContentValuesQuery, ContentTypeListQuery};
